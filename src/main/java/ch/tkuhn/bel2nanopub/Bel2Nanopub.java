package ch.tkuhn.bel2nanopub;

import static ch.tkuhn.bel2nanopub.ThirdPartyVocabulary.bfoHasPart;
import static ch.tkuhn.bel2nanopub.ThirdPartyVocabulary.pavVersion;
import static ch.tkuhn.bel2nanopub.ThirdPartyVocabulary.provHadPrimarySource;
import static ch.tkuhn.bel2nanopub.ThirdPartyVocabulary.provValue;
import static ch.tkuhn.bel2nanopub.ThirdPartyVocabulary.provWasDerivedFrom;
import static ch.tkuhn.bel2nanopub.ThirdPartyVocabulary.provWasQuotedFrom;
import static ch.tkuhn.bel2nanopub.ThirdPartyVocabulary.pubmedNs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.nanopub.Nanopub;
import org.nanopub.NanopubCreator;
import org.nanopub.NanopubUtils;
import org.nanopub.NanopubVocab;
import org.openbel.bel.model.BELAnnotation;
import org.openbel.bel.model.BELAnnotationDefinition;
import org.openbel.bel.model.BELCitation;
import org.openbel.bel.model.BELDocument;
import org.openbel.bel.model.BELDocumentHeader;
import org.openbel.bel.model.BELEvidence;
import org.openbel.bel.model.BELNamespaceDefinition;
import org.openbel.bel.model.BELParseErrorException;
import org.openbel.bel.model.BELStatement;
import org.openbel.bel.model.BELStatementGroup;
import org.openbel.framework.common.bel.parser.BELParseResults;
import org.openbel.framework.common.bel.parser.BELParser;
import org.openbel.framework.common.model.Parameter;
import org.openbel.framework.common.model.Statement;
import org.openbel.framework.common.model.Term;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Bel2Nanopub {

	@com.beust.jcommander.Parameter(description = "input-bel-files", required = true)
	private List<File> inputFiles = new ArrayList<File>();

	@com.beust.jcommander.Parameter(names = "-c", description = "Orcid ID of creator")
	private String creatorId = null;

	@com.beust.jcommander.Parameter(names = "-t", description = "Timestamp (use only for regression testing)")
	private String timestamp = null;

	@com.beust.jcommander.Parameter(names = "-u", description = "URI of the BEL document")
	private String belDocUri = null;

	public static void main(String[] args) {
		Bel2Nanopub obj = new Bel2Nanopub();
		JCommander jc = new JCommander(obj);
		try {
			jc.parse(args);
		} catch (ParameterException ex) {
			jc.usage();
			System.exit(1);
		}
		try {
			obj.run();
			if (!obj.getParseExceptions().isEmpty()) {
				System.err.println("ERRORS:");
				for (BELParseErrorException ex : obj.getParseExceptions()) {
					System.err.println(ex.getMessage());
				}
				System.exit(1);
			}
			boolean isFirst = true;
			for (Result r : obj.getResults()) {
				if (!isFirst) {
					System.out.print("\n\n");
				}
				isFirst = false;
				if (r.getException() == null) {
					NanopubUtils.writeToStream(r.getNanopub(), System.out, RDFFormat.TRIG);
				} else {
					System.err.println("ERROR:");
					System.err.println(r.getException().getMessage());
				}
			}
			System.err.println(obj.getNanopubs().size() + " nanopub(s) created");
			System.err.println(obj.getTransformExceptions().size() + " error(s)");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private int bnodeCount = 0;
	private BELDocument belDoc;
	private Resource belDocResource;
	private List<Nanopub> nanopubs = new ArrayList<Nanopub>();
	private List<Result> results = new ArrayList<Result>();
	private List<Bel2NanopubException> transformExceptions = new ArrayList<Bel2NanopubException>();
	private List<BELParseErrorException> parseExceptions = new ArrayList<BELParseErrorException>();

	private Map<String,String> namespaceMap = new HashMap<String,String>();
	private Map<String,String> annotationMap = new HashMap<String,String>();

	private ValueFactoryImpl vf = new ValueFactoryImpl();


	public Bel2Nanopub(File... belDocs) {
		for (File f : belDocs) {
			inputFiles.add(f);
		}
	}

	private Bel2Nanopub() {
	}

	public void run() {
		for (File f : inputFiles) {
			run(f);
		}
	}

	private void run(File belFile) {
		BELParseResults belParse = BELParser.parse(Utils.readFile(belFile));

		if (!belParse.getSyntaxErrors().isEmpty()) {
			parseExceptions.addAll(belParse.getSyntaxErrors());
			return;
		}
		belDoc = belParse.getDocument();

		if (belDocUri != null) {
			belDocResource = new URIImpl(belDocUri);
		} else {
			belDocResource = newBNode();
		}

		readNamespaces();
		readAnnotations();

		for (BELStatementGroup g : belDoc.getBelStatementGroups()) {
			for (BELStatement bst : g.getStatements()) {
				NanopubCreator npCreator = new NanopubCreator("http://www.tkuhn.ch/bel2nanopub/");

				// Aliases:
				npCreator.addNamespace("hasPart", "http://purl.obolibrary.org/obo/BFO_0000051");
				npCreator.addNamespace("occursIn", "http://purl.obolibrary.org/obo/BFO_0000066");
				npCreator.addNamespace("hasAgent", "http://semanticscience.org/resource/SIO_000139");
				npCreator.addNamespace("hasAnnotation", "http://semanticscience.org/resource/SIO_000255");
				npCreator.addNamespace("geneProductOf", "http://purl.obolibrary.org/obo/RO_0002204");
				npCreator.addNamespace("ProteinComplex", "http://amigo.geneontology.org/amigo/term/GO:0043234");
				npCreator.addNamespace("Protein", "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI_36080");
				npCreator.addNamespace("RNA", "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI_33697");
				npCreator.addNamespace("microRNA", "http://purl.obolibrary.org/obo/SO_0000276");
				npCreator.addNamespace("proteinModification", "http://www.ebi.ac.uk/ontology-lookup/?termId=MOD:00000");
				// TODO only add aliases that are needed for the given nanopub

				npCreator.addNamespace("rdfs", RDFS.NAMESPACE);
				npCreator.addNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
				npCreator.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
				npCreator.addNamespace("dct", "http://purl.org/dc/terms/");
				npCreator.addNamespace("dce", "http://purl.org/dc/elements/1.1/");
				npCreator.addNamespace("np", "http://www.nanopub.org/nschema#");
				npCreator.addNamespace("belv", BelRdfVocabulary.BELV_NS);
				npCreator.addNamespace("prov", "http://www.w3.org/ns/prov#");
				npCreator.addNamespace("semsc", "http://semanticscience.org/resource/");
				try {
					processBelStatement(bst, npCreator);
				} catch (Bel2NanopubException ex) {
					transformExceptions.add(ex);
					results.add(new Result(bst, ex));
					continue;
				}
				if (creatorId != null) {
					npCreator.addNamespace("pav", "http://purl.org/pav/");
					npCreator.addNamespace("orcid", "http://orcid.org/");
					npCreator.addCreator(creatorId);
				}
				if (timestamp != null) {
					npCreator.addTimestamp(DatatypeConverter.parseDateTime(timestamp).getTime());
				}
				try {
					Nanopub np = npCreator.finalizeTrustyNanopub(timestamp == null);
					nanopubs.add(np);
					results.add(new Result(bst, np));
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	private void readNamespaces() {
		for (BELNamespaceDefinition d : belDoc.getNamespaceDefinitions()) {
			namespaceMap.put(d.getPrefix(), d.getResourceLocation());
		}
	}

	private void readAnnotations() {
		for (BELAnnotationDefinition d : belDoc.getAnnotationDefinitions()) {
			if (!d.getAnnotationType().getDisplayValue().equals("URL")) continue;
			annotationMap.put(d.getName(), d.getValue());
		}
	}

	private void processBelStatement(BELStatement belStatement, NanopubCreator npCreator) throws Bel2NanopubException {
		Statement st = BELParser.parseStatement(belStatement.getStatementSyntax());
		boolean reify = !belStatement.getAnnotations().isEmpty();
		List<Resource> resources = processBelStatement(st, npCreator, reify);
		for (Resource r : resources) {
			for (BELAnnotation ann : belStatement.getAnnotations()) {
				processAnnotation(ann, r, npCreator);
			}
		}
		processEvidence(belStatement, npCreator);
		processDocumentHeader(belStatement, npCreator);
	}

	private void processEvidence(BELStatement belStatement, NanopubCreator npCreator) throws Bel2NanopubException {
		BELCitation cit = belStatement.getCitation();
		URI citUri = null;
		if (cit != null) {
			if (!cit.getType().toLowerCase().equals("pubmed")) {
				throw new Bel2NanopubException("Unsupported citation type: " + cit.getType() + " " + cit.getComment());
			}
			citUri = new URIImpl(pubmedNs + cit.getReference());
			npCreator.addProvenanceStatement(provHadPrimarySource, citUri);
			npCreator.addNamespace("pubmed", pubmedNs);
		}
		BELEvidence ev = belStatement.getEvidence();
		if (ev != null) {
			Literal l = vf.createLiteral(ev.getEvidenceLine());
			BNode q = newBNode();
			npCreator.addProvenanceStatement(q, provValue, l);
			npCreator.addProvenanceStatement(provWasDerivedFrom, q);
			if (citUri != null) {
				npCreator.addProvenanceStatement(q, provWasQuotedFrom, citUri);
			}
		}
	}

	private void processDocumentHeader(BELStatement belStatement, NanopubCreator npCreator) {
		BELDocumentHeader h = belDoc.getDocumentHeader();
		npCreator.addProvenanceStatement(provWasDerivedFrom, belDocResource);
		if (h.getName() != null) {
			npCreator.addProvenanceStatement(belDocResource, DC.TITLE, vf.createLiteral(h.getName()));
		}
		if (h.getDescription() != null) {
			npCreator.addProvenanceStatement(belDocResource, DC.DESCRIPTION, vf.createLiteral(h.getDescription()));
		}
		if (h.getCopyright() != null) {
			npCreator.addProvenanceStatement(belDocResource, DC.RIGHTS, vf.createLiteral(h.getCopyright()));
		}
		if (h.getLicense() != null) {
			npCreator.addProvenanceStatement(belDocResource, DCTERMS.LICENSE, vf.createLiteral(h.getLicense()));
		}
		if (h.getVersion() != null) {
			npCreator.addProvenanceStatement(belDocResource, pavVersion, vf.createLiteral(h.getVersion()));
		}
		if (h.getAuthor() != null || h.getContactInfo() != null) {
			BNode author = newBNode();
			npCreator.addProvenanceStatement(belDocResource, NanopubVocab.PAV_AUTHOREDBY, author);
			if (h.getAuthor() != null) {
				npCreator.addProvenanceStatement(author, RDFS.LABEL, vf.createLiteral(h.getAuthor()));
			}
			if (h.getContactInfo() != null) {
				npCreator.addProvenanceStatement(author, RDFS.COMMENT, vf.createLiteral(h.getContactInfo()));
			}
		}
	}

	private Resource processBelObject(org.openbel.framework.common.model.Statement.Object obj, NanopubCreator npCreator)
			 throws Bel2NanopubException {
		if (obj.getTerm() != null) {
			return processBelTerm(obj.getTerm(), npCreator);
		} else {
			List<Resource> resources = processBelStatement(obj.getStatement(), npCreator, true);
			if (resources.size() != 1) {
				throw new Bel2NanopubException("Exactly one resource expected");
			}
			return resources.get(0);
		}
	}

	private Resource processBelTerm(Term term, NanopubCreator npCreator) throws Bel2NanopubException {
		Resource r;
		String funcAbbrev = Utils.getFunctionAbbrev(term);
		if (funcAbbrev.matches("bp|path")) {
			// Direct mapping
			if (term.getParameters().size() != 1 || term.getTerms().size() != 0) {
				throw new Bel2NanopubException("Unexpected parameters or child terms");
			}
			r = getUriFromParam(term.getParameters().get(0), npCreator);
		} else {
			URI actUri = IdSchemes.makeUri("activity", funcAbbrev, npCreator);
			if (funcAbbrev.equals("p") && term.getTerms().size() > 0) {
				// protein variant
				r = handleProteinVariantTerm(term, npCreator);
			} else if (funcAbbrev.equals("complex")) {
				if (term.getTerms().size() > 0) {
					r = handleCompoundAbundanceTerm(term, npCreator);
				} else {
					r = handleSimpleAbundanceTerm(term, npCreator);
				}
				npCreator.addAssertionStatement(r, RDF.TYPE, ThirdPartyVocabulary.goProteinComplex);
			} else if (funcAbbrev.equals("composite")) {
				r = handleCompoundAbundanceTerm(term, npCreator);
			} else if (funcAbbrev.matches("a|g")) {
				r = handleSimpleAbundanceTerm(term, npCreator);
			} else if (funcAbbrev.matches("p")) {
				r = newBNode();
				npCreator.addAssertionStatement(r, RDF.TYPE, ThirdPartyVocabulary.chebiProtein);
				URI uri = handleSimpleAbundanceTerm(term, npCreator);
				npCreator.addAssertionStatement(r, ThirdPartyVocabulary.roGeneProductOf, uri);
			} else if (funcAbbrev.matches("r")) {
				r = newBNode();
				npCreator.addAssertionStatement(r, RDF.TYPE, ThirdPartyVocabulary.chebiRna);
				URI uri = handleSimpleAbundanceTerm(term, npCreator);
				npCreator.addAssertionStatement(r, ThirdPartyVocabulary.roGeneProductOf, uri);
			} else if (funcAbbrev.matches("m")) {
				r = newBNode();
				npCreator.addAssertionStatement(r, RDF.TYPE, ThirdPartyVocabulary.soMicroRna);
				URI uri = handleSimpleAbundanceTerm(term, npCreator);
				npCreator.addAssertionStatement(r, ThirdPartyVocabulary.roGeneProductOf, uri);
			} else if (funcAbbrev.matches("tloc|sec|surf|deg|rxn")) {
				r = handleTransformationTerm(term, npCreator);
			} else if (actUri != null) {
				r = handleActivityTerm(term, npCreator);
				npCreator.addAssertionStatement(r, RDF.TYPE, actUri);
			} else {
				throw new Bel2NanopubException("Unknown function: " + funcAbbrev);
			}
		}
		return r;
	}

	private Resource handleProteinVariantTerm(Term protTerm, NanopubCreator npCreator) throws Bel2NanopubException {
		BNode bn = newBNode();
		Parameter protParam = protTerm.getParameters().get(0);
		URI pUri = getUriFromParam(protParam, npCreator);
		if (pUri != null) {
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.variantOf, pUri);
		}
		for (Term varTerm : protTerm.getTerms()) {
			String modAbbrev = varTerm.getFunctionEnum().getAbbreviation();
			String varString = varTerm.toBELShortForm().replaceFirst("^.*\\((.*)\\).*$", "$1");
			if (modAbbrev.equals("pmod")) {
				npCreator.addAssertionStatement(bn, RDF.TYPE, ThirdPartyVocabulary.modProteinModification);
				String v = varString.substring(0, 1);
				URI modtypeUri = IdSchemes.makeUri("psimod", v, npCreator);
				if (modtypeUri == null) {
					throw new Bel2NanopubException("Cannot resolve modifier type '" + v + "'");
				}
				if (modtypeUri != null) {
					npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasModificationType, modtypeUri);
				}
			} else  {
				String var = BelRdfVocabulary.getNormalizedVariant(modAbbrev);
				if (var == null) {
					throw new Bel2NanopubException("Unknown protein variant: " + var);
				}
				npCreator.addAssertionStatement(bn, RDF.TYPE, ThirdPartyVocabulary.modProteinModification);
				// TODO What to do with protein variants? (they are ignored by bel2rdf)
				if (var.equals(BelRdfVocabulary.getNormalizedVariant("sub"))) {
					npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasSubstitution, vf.createLiteral(varString));
				} else {
					throw new Bel2NanopubException("Unsupported protein variant: " + var);
				}
			}
		}
		return bn;
	}

	private URI handleSimpleAbundanceTerm(Term term, NanopubCreator npCreator) throws Bel2NanopubException {
		if (!term.getTerms().isEmpty()) {
			throw new Bel2NanopubException("Unexpected presence of sub-terms: " + term.getFunctionEnum());
		}
		if (term.getParameters().size() != 1) {
			throw new Bel2NanopubException("Exactly one parameter expected: " + term.getFunctionEnum());
		}
		return getUriFromParam(term.getParameters().get(0), npCreator);
	}

	private Resource handleCompoundAbundanceTerm(Term term, NanopubCreator npCreator) throws Bel2NanopubException {
		BNode bn = newBNode();
		if (!term.getParameters().isEmpty()) {
			throw new Bel2NanopubException("Unexpected presence of both, terms and parameters: " + term.getFunctionEnum());
		}
		if (term.getTerms().isEmpty()) {
			throw new Bel2NanopubException("Empty abundance term: " + term.getFunctionEnum());
		}
		npCreator.addNamespace("obo", ThirdPartyVocabulary.oboNs);
		for (Term child : term.getTerms()) {
			Resource ch = processBelTerm(child, npCreator);
			npCreator.addAssertionStatement(bn, bfoHasPart, ch);
		}
		return bn;
	}

	private Resource handleActivityTerm(Term term, NanopubCreator npCreator) throws Bel2NanopubException {
		if (term.getTerms().size() != 1) {
			throw new Bel2NanopubException("Expected exactly 1 term for activity: " + term.getFunctionEnum());
		}
		if (term.getParameters().size() > 0) {
			throw new Bel2NanopubException("Unexpected parameters for activity: " + term.getFunctionEnum());
		}
		BNode bn = newBNode();
		Resource ch = processBelTerm(term.getTerms().get(0), npCreator);
		npCreator.addAssertionStatement(bn, ThirdPartyVocabulary.sioHasAgent, ch);
		return bn;
	}

	private Resource handleTransformationTerm(Term term, NanopubCreator npCreator) throws Bel2NanopubException {
		String funcAbbrev = term.getFunctionEnum().getAbbreviation();
		int tc = term.getTerms().size();
		int pc = term.getParameters().size();
		BNode bn = newBNode();
		if ("tloc".equals(funcAbbrev)) {
			if (tc != 1 || pc != 2) {
				throw new Bel2NanopubException("Invalid format for 'tloc'");
			}
			Resource r = processBelTerm(term.getTerms().get(0), npCreator);
			URI from = getUriFromParam(term.getParameters().get(0), npCreator);
			URI to = getUriFromParam(term.getParameters().get(1), npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationOf, r);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationFrom, from);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationTo, to);
		} else if ("sec".equals(funcAbbrev)) {
			if (tc != 1 || pc != 0) {
				throw new Bel2NanopubException("Invalid format for 'sec'");
			}
			Resource r = processBelTerm(term.getTerms().get(0), npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationOf, r);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationFrom, ThirdPartyVocabulary.goIntracellular);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationTo, ThirdPartyVocabulary.goExtracellularRegion);
		} else if ("surf".equals(funcAbbrev)) {
			if (tc != 1 || pc != 0) {
				throw new Bel2NanopubException("Invalid format for 'surf'");
			}
			Resource r = processBelTerm(term.getTerms().get(0), npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationOf, r);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationFrom, ThirdPartyVocabulary.goIntracellular);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.translocationTo, ThirdPartyVocabulary.goCellSurface);
		} else if ("deg".equals(funcAbbrev)) {
			if (tc != 1 || pc != 0) {
				throw new Bel2NanopubException("Invalid format for 'deg'");
			}
			Resource r = processBelTerm(term.getTerms().get(0), npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.degradationOf, r);
		} else if ("rxn".equals(funcAbbrev)) {
			if (tc != 2 || pc != 0) {
				throw new Bel2NanopubException("Invalid format for 'rxn'");
			}
			npCreator.addAssertionStatement(bn, RDF.TYPE, ThirdPartyVocabulary.sioBiochemicalReaction);
			for (Term reactant : term.getTerms().get(0).getTerms()) {
				Resource r = processBelTerm(reactant, npCreator);
				npCreator.addAssertionStatement(bn, ThirdPartyVocabulary.sioHasInput, r);
			}
			for (Term product : term.getTerms().get(1).getTerms()) {
				Resource r = processBelTerm(product, npCreator);
				npCreator.addAssertionStatement(bn, ThirdPartyVocabulary.sioHasOutput, r);
			}
		} else {
			throw new Bel2NanopubException("Unexpected unrecognized transformation function: " + funcAbbrev);
		}
		return bn;
	}

	private URI getUriFromParam(Parameter param, NanopubCreator npCreator) throws Bel2NanopubException {
		if (param == null || param.getNamespace() == null) {
			throw new Bel2NanopubException("Invalid parameter: " + param);
		}
		String prefix = param.getNamespace().getPrefix();
		String belNs = namespaceMap.get(prefix);
		if (belNs == null) {
			throw new Bel2NanopubException("Unknown namespace: " + prefix);
		}
		String v = param.getValue();
		URI uri = IdSchemes.makeUri(belNs, v, npCreator, prefix);
		if (uri == null) {
			throw new Bel2NanopubException("Cannot resolve entity '" + v + "' in namespace '" + belNs + "'");
		}
		return uri;
	}

	private void processAnnotation(BELAnnotation ann, Resource node, NanopubCreator npCreator) throws Bel2NanopubException {
		String annN = ann.getAnnotationDefinition().getName();
		String annNs = annotationMap.get(annN);
		if (annNs == null) {
			for (String annV : ann.getValues()) {
				BNode annBn = newBNode();
				Literal annType = vf.createLiteral(annN);
				Literal annValue = vf.createLiteral(annV);
				npCreator.addAssertionStatement(node, ThirdPartyVocabulary.sioHasAnnotation, annBn);
				npCreator.addAssertionStatement(annBn, DCTERMS.SUBJECT, annType);
				npCreator.addAssertionStatement(annBn, RDF.VALUE, annValue);
			}
		} else {
			for (String annV : ann.getValues()) {
				URI annUri = IdSchemes.makeUri(annNs, annV, npCreator, annN);
				if (annUri == null) {
					throw new Bel2NanopubException("Cannot resolve entity '" + annV + "' in namespace '" + annNs + "'");
				}
				IdScheme sc = IdSchemes.getScheme(annNs);
				if (sc != null) {
					npCreator.addNamespace("obo", ThirdPartyVocabulary.oboNs);
					npCreator.addAssertionStatement(node, ThirdPartyVocabulary.bfoOccursIn, annUri);
				} else {
					BNode annBn = newBNode();
					Literal annType = vf.createLiteral(annN);
					npCreator.addAssertionStatement(node, ThirdPartyVocabulary.sioHasAnnotation, annBn);
					npCreator.addAssertionStatement(annBn, DCTERMS.SUBJECT, annType);
					npCreator.addAssertionStatement(annBn, RDF.VALUE, annUri);
				}
			}
		}
	}

	private List<Resource> processBelStatement(Statement statement, NanopubCreator npCreator, boolean reify) throws Bel2NanopubException {
		List<Resource> r = new ArrayList<Resource>();
		if (statement.getRelationshipType() == null) {
			r.add(processBelTerm(statement.getSubject(), npCreator));
		} else {
			Resource subj = processBelTerm(statement.getSubject(), npCreator);
			String relN = statement.getRelationshipType().getDisplayValue();
			URI rel;
			if (relN.equals("isA")) {
				rel = RDFS.SUBCLASSOF;
			} else if (relN.equals("hasMember")) {
				rel = RDFS.MEMBER;
			} else if (relN.equals("hasComponent")) {
				rel = ThirdPartyVocabulary.bfoHasPart;
			} else {
				rel = BelRdfVocabulary.getRel(relN);
			}
			if (rel == null) {
				throw new Bel2NanopubException("Unknown relationship: " + relN);
			}
			List<Resource> objs = new ArrayList<Resource>();
			if (relN.matches("hasMembers|hasComponents")) {
				Term term = statement.getObject().getTerm();
				if (!Utils.getFunctionAbbrev(term).equals("list")) {
					throw new Bel2NanopubException("List expected for multi-relation: " + relN);
				}
				if (term.getParameters().size() > 0) {
					throw new Bel2NanopubException("Invalid format for 'list'");
				}
				for (Term element : term.getTerms()) {
					objs.add(processBelTerm(element, npCreator));
				}
			} else {
				objs.add(processBelObject(statement.getObject(), npCreator));
			}
			for (Resource obj : objs) {
				if (reify) {
					BNode bn = newBNode();
					npCreator.addAssertionStatement(bn, RDF.TYPE, RDF.STATEMENT);
					npCreator.addAssertionStatement(bn, RDF.SUBJECT, subj);
					npCreator.addAssertionStatement(bn, RDF.PREDICATE, rel);
					npCreator.addAssertionStatement(bn, RDF.OBJECT, obj);
					r.add(bn);
				} else {
					npCreator.addAssertionStatement(subj, rel, obj);
				}
			}
		}
		Literal label = new LiteralImpl(statement.toBELShortForm());
		npCreator.addAssertionStatement(npCreator.getAssertionUri(), RDFS.LABEL, label);
		return r;
	}

	private BNode newBNode() {
		return new BNodeImpl("" + bnodeCount++);
	}

	public List<Nanopub> getNanopubs() {
		return nanopubs;
	}

	public List<Bel2NanopubException> getTransformExceptions() {
		return transformExceptions;
	}

	public List<BELParseErrorException> getParseExceptions() {
		return parseExceptions;
	}

	public List<Result> getResults() {
		return results;
	}


	public static class Result {

		private final BELStatement belSt;
		private final Nanopub nanopub;
		private final Bel2NanopubException exception;

		private Result(BELStatement belSt, Nanopub nanopub) {
			this.belSt = belSt;
			this.nanopub = nanopub;
			this.exception = null;
		}
		private Result(BELStatement belSt, Bel2NanopubException exception) {
			this.belSt = belSt;
			this.nanopub = null;
			this.exception = exception;
		}

		public BELStatement getBelStatement() {
			return belSt;
		}

		public Nanopub getNanopub() {
			return nanopub;
		}

		public Bel2NanopubException getException() {
			return exception;
		}

	}

}
