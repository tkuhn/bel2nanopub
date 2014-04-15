package ch.tkuhn.bel2nanopub;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nanopub.Nanopub;
import org.nanopub.NanopubCreator;
import org.nanopub.NanopubUtils;
import org.openbel.bel.model.BELAnnotation;
import org.openbel.bel.model.BELAnnotationDefinition;
import org.openbel.bel.model.BELCitation;
import org.openbel.bel.model.BELDocument;
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
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
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

	public static void main(String[] args) {
		Bel2Nanopub obj = new Bel2Nanopub();
		JCommander jc = new JCommander(obj);
		try {
			jc.parse(args);
		} catch (ParameterException ex) {
			jc.usage();
			System.exit(1);
		}
		obj.run();
		System.out.println("---");
		System.out.println(obj.getNanopubs().size() + " nanopub(s) created");
		System.out.println(obj.errors + " error(s)");
	}

	private static final URI provValue = new URIImpl("http://www.w3.org/ns/prov#value");
	private static final URI provWasQuotedFrom = new URIImpl("http://www.w3.org/ns/prov#wasQuotedFrom");
	private static final URI provWasDerivedFrom = new URIImpl("http://www.w3.org/ns/prov#wasDerivedFrom");
	private static final URI provHadPrimarySource = new URIImpl("http://www.w3.org/ns/prov#hadPrimarySource");

	private int bnodeCount = 0;
	private BELDocument belDoc;
	private List<Nanopub> nanopubs = new ArrayList<Nanopub>();
	private int errors = 0;

	private Map<String,URI> namespaceMap = new HashMap<String,URI>();
	private Map<String,URI> annotationMap = new HashMap<String,URI>();

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
		BELParseResults belParse = BELParser.parse(readFile(belFile));

		if (!belParse.getSyntaxErrors().isEmpty()) {
			System.err.println("ERRORS:");
			for (BELParseErrorException ex : belParse.getSyntaxErrors()) {
				System.err.println(ex.getMessage());
			}
			System.exit(1);
		}
		belDoc = belParse.getDocument();

		readNamespaces();
		readAnnotations();

		for (BELStatementGroup g : belDoc.getBelStatementGroups()) {
			for (BELStatement bst : g.getStatements()) {
				System.out.println("---");
				System.out.println("BEL: " + bst.getStatementSyntax());
				NanopubCreator npCreator = new NanopubCreator("http://www.tkuhn.ch/bel2nanopub/");
				npCreator.addNamespace("rdfs", RDFS.NAMESPACE);
				npCreator.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
				npCreator.addNamespace("dc", "http://purl.org/dc/terms/");
				npCreator.addNamespace("np", "http://www.nanopub.org/nschema#");
				npCreator.addNamespace("belv", BelRdfVocabulary.BELV_NS);
				npCreator.addNamespace("prov", "http://www.w3.org/ns/prov#");
				try {
					processBelStatement(bst, npCreator);
				} catch (Bel2NanopubException ex) {
					errors++;
					System.err.println(ex.getMessage());
					continue;
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
				if (creatorId != null) {
					npCreator.addNamespace("pav", "http://swan.mindinformatics.org/ontologies/1.2/pav/");
					npCreator.addNamespace("orcid", "http://orcid.org/");
					npCreator.addCreator(creatorId);
				}
				try {
					Nanopub np = npCreator.finalizeTrustyNanopub(true);
					System.out.println("NANOPUB:");
					NanopubUtils.writeToStream(np, System.out, RDFFormat.TRIG);
					nanopubs.add(np);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	private void readNamespaces() {
		for (BELNamespaceDefinition d : belDoc.getNamespaceDefinitions()) {
			String uriString = d.getResourceLocation().replaceFirst("\\.belns$", "");
			if (!uriString.endsWith("/")) uriString += "/";
			namespaceMap.put(d.getPrefix(), new URIImpl(uriString));
		}
	}

	private void readAnnotations() {
		for (BELAnnotationDefinition d : belDoc.getAnnotationDefinitions()) {
			if (!d.getAnnotationType().getDisplayValue().equals("URL")) continue;
			String uriString = d.getValue().replaceFirst("\\.belanno$", "");
			if (!uriString.endsWith("/")) uriString += "/";
			annotationMap.put(d.getName(), new URIImpl(uriString));
		}
	}

	private BNode processBelStatement(BELStatement belStatement, NanopubCreator npCreator) throws Bel2NanopubException {
		Statement st = BELParser.parseStatement(belStatement.getStatementSyntax());
		BNode bn = processBelStatement(st, npCreator);
		for (BELAnnotation ann : belStatement.getAnnotations()) {
			String annN = ann.getAnnotationDefinition().getName();
			URI annNs = annotationMap.get(annN);
			if (annNs == null) {
				for (String annV : ann.getValues()) {
					Literal annL = vf.createLiteral(annV);
					npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasAnnotation, annL);
				}
			} else {
				npCreator.addNamespace(annN.toLowerCase(), annNs);
				for (String annV : ann.getValues()) {
					URI annUri = new URIImpl(annNs + encodeUrlString(annV));
					npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasAnnotation, annUri);
				}
			}
		}
		BELCitation cit = belStatement.getCitation();
		URI citUri = null;
		if (cit != null) {
			if (!cit.getType().toLowerCase().equals("pubmed")) {
				throw new Bel2NanopubException("Unsupported citation type: " + cit.getType() + " " + cit.getComment());
			}
			citUri = new URIImpl("http://www.ncbi.nlm.nih.gov/pubmed/" + cit.getReference());
			npCreator.addProvenanceStatement(provHadPrimarySource, citUri);
			npCreator.addNamespace("pubmed", "http://www.ncbi.nlm.nih.gov/pubmed/");
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
		return bn;
	}

	private BNode processBelObject(org.openbel.framework.common.model.Statement.Object obj, NanopubCreator npCreator)
			 throws Bel2NanopubException{
		if (obj.getTerm() != null) {
			return processBelTerm(obj.getTerm(), npCreator);
		} else {
			return processBelStatement(obj.getStatement(), npCreator);
		}
	}

	private BNode processBelTerm(Term term, NanopubCreator npCreator) throws Bel2NanopubException {
		BNode bn = newBNode();
		npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.term);
		String funcAbbrev = term.getFunctionEnum().getAbbreviation();
		if (funcAbbrev == null) funcAbbrev = term.getFunctionEnum().getDisplayValue();
		URI funcUri = BelRdfVocabulary.getFunction(funcAbbrev);
		if (funcUri == null) {
			throw new Bel2NanopubException("Unknown function: " + funcAbbrev);
		}
		URI actUri = BelRdfVocabulary.getActivity(funcAbbrev);
		if (actUri != null) {
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasActivityType, actUri);
		}
		if (isProteinVariantTerm(funcUri, term)) {
			handleProteinVariantTerm(bn, term, npCreator);
		} else {
			npCreator.addAssertionStatement(bn, RDF.TYPE, funcUri);
			handleNormalTerm(bn, term, npCreator);
		}
		npCreator.addAssertionStatement(bn, RDFS.LABEL, new LiteralImpl(term.toBELShortForm()));
		return bn;
	}

	private boolean isProteinVariantTerm(URI funcUri, Term term) {
		if (!funcUri.equals(BelRdfVocabulary.getFunction("p"))) return false;
		return (term.getTerms().size() > 0);
	}

	private void handleProteinVariantTerm(BNode bn, Term protTerm, NanopubCreator npCreator) throws Bel2NanopubException {
		Parameter protParam = protTerm.getParameters().get(0);
		BNode n = newBNode();
		npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasChild, n);
		npCreator.addAssertionStatement(n, RDF.TYPE, BelRdfVocabulary.term);
		npCreator.addAssertionStatement(n, RDF.TYPE, BelRdfVocabulary.getFunction("p"));
		npCreator.addAssertionStatement(n, RDFS.LABEL, new LiteralImpl("p(" + protParam.toBELShortForm() + ")"));
		URI pUri = getUriFromParam(protParam, npCreator);
		if (pUri != null) {
			npCreator.addAssertionStatement(n, BelRdfVocabulary.hasConcept, pUri);
		}
		for (Term varTerm : protTerm.getTerms()) {
			String modAbbrev = varTerm.getFunctionEnum().getAbbreviation();
			String varString = varTerm.toBELShortForm().replaceFirst("^.*\\((.*)\\).*$", "$1");
			if (modAbbrev.equals("pmod")) {
				npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.modifiedProteinAbundance);
				URI modtypeUri = BelRdfVocabulary.getModificationType(varString.substring(0, 1));
				if (modtypeUri != null) {
					npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasModificationType, modtypeUri);
				}
				npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasModification, vf.createLiteral(varString));
			} else  {
				String var = BelRdfVocabulary.getNormalizedVariant(modAbbrev);
				if (var == null) {
					throw new Bel2NanopubException("Unknown protein variant: " + var);
				}
				npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.proteinVariantAbundance);
				// TODO What to do with protein variants? (they are ignored by bel2rdf)
				if (var.equals(BelRdfVocabulary.getNormalizedVariant("sub"))) {
					npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasSubstitution, vf.createLiteral(varString));
				} else {
					throw new Bel2NanopubException("Unsupported protein variant: " + var);
				}
			}
		}
	}

	private void handleNormalTerm(BNode bn, Term term, NanopubCreator npCreator) throws Bel2NanopubException {
		for (Term child : term.getTerms()) {
			BNode ch = processBelTerm(child, npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasChild, ch);
		}
		for (Parameter p : term.getParameters()) {
			URI cUri = getUriFromParam(p, npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasConcept, cUri);
		}
	}

	private URI getUriFromParam(Parameter param, NanopubCreator npCreator) throws Bel2NanopubException {
		if (param == null || param.getNamespace() == null) {
			throw new Bel2NanopubException("Invalid parameter: " + param);
		}
		String prefix = param.getNamespace().getPrefix();
		URI ns = namespaceMap.get(prefix);
		if (ns == null) {
			throw new Bel2NanopubException("Unknown namespace: " + prefix);
		}
		npCreator.addNamespace(prefix.toLowerCase(), ns);
		return new URIImpl(ns + encodeUrlString(param.getValue()));
	}

	private BNode processBelStatement(Statement statement, NanopubCreator npCreator) throws Bel2NanopubException {
		BNode bn = newBNode();
		if (statement.getRelationshipType() == null) {
			bn = processBelTerm(statement.getSubject(), npCreator);
		} else {
			npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.statement);
			BNode subj = processBelTerm(statement.getSubject(), npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasSubject, subj);
			String relN = statement.getRelationshipType().getDisplayValue();
			URI rel = BelRdfVocabulary.getRel(relN);
			if (rel == null) {
				throw new Bel2NanopubException("Unknown relationship: " + relN);
			}
			rel.toString();  // Raise null pointer exception
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasRelationship, rel);
			if (statement.getObject() != null) {
				BNode obj = processBelObject(statement.getObject(), npCreator);
				npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasObject, obj);
			}
		}
		npCreator.addAssertionStatement(bn, RDFS.LABEL, new LiteralImpl(statement.toBELShortForm()));
		return bn;
	}

	private BNode newBNode() {
		return new BNodeImpl("" + bnodeCount++);
	}

	public List<Nanopub> getNanopubs() {
		return nanopubs;
	}

	private static String readFile(File file) {
		try {
			byte[] encoded = Files.readAllBytes(file.toPath());
			return Charset.forName("UTF-8").decode(ByteBuffer.wrap(encoded)).toString();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static String encodeUrlString(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
