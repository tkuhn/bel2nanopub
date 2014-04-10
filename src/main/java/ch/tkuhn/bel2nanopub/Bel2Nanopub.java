package ch.tkuhn.bel2nanopub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.trustyuri.rdf.TransformNanopub;

import org.nanopub.Nanopub;
import org.nanopub.NanopubCreator;
import org.nanopub.NanopubUtils;
import org.openbel.bel.model.BELAnnotation;
import org.openbel.bel.model.BELParseErrorException;
import org.openbel.bel.model.BELStatement;
import org.openbel.bel.model.BELStatementGroup;
import org.openbel.framework.common.bel.parser.BELParseResults;
import org.openbel.framework.common.bel.parser.BELParser;
import org.openbel.framework.common.model.Parameter;
import org.openbel.framework.common.model.Statement;
import org.openbel.framework.common.model.Term;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;

public class Bel2Nanopub {

	public static void main(String[] args) {
		String belDoc = readFile(args[0]);
		Bel2Nanopub obj = new Bel2Nanopub(belDoc);
		System.out.println(obj.getNanopubs().size() + " nanopub(s) created");
	}

	private int bnodeCount = 0;
	private List<Nanopub> nanopubs = new ArrayList<Nanopub>();

	public Bel2Nanopub(String belDoc) {
		BELParseResults result = BELParser.parse(belDoc);

		if (!result.getSyntaxErrors().isEmpty()) {
			System.err.println("ERRORS:");
			for (BELParseErrorException ex : result.getSyntaxErrors()) {
				System.err.println(ex.getMessage());
			}
			System.exit(1);
		}

		for (BELStatementGroup g : result.getDocument().getBelStatementGroups()) {
			for (BELStatement bst : g.getStatements()) {
				NanopubCreator npCreator = new NanopubCreator("http://www.tkuhn.ch/bel2nanopub/");
				npCreator.addNamespace("belv", BelRdfVocabulary.BELV_NS);
				npCreator.addNamespace("np", "http://www.nanopub.org/nschema#");
				npCreator.addNamespace("rdfs", RDFS.NAMESPACE);
				processBelStatement(bst, npCreator);
				try {
					Nanopub np = TransformNanopub.transform(npCreator.finalizeNanopub());
					System.out.println("NANOPUB:");
					NanopubUtils.writeToStream(np, System.out, RDFFormat.TRIG);
					nanopubs.add(np);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("---");
			}
		}
	}

	private BNode processBelStatement(BELStatement belStatement, NanopubCreator npCreator) {
		String s = belStatement.getStatementSyntax();
		Statement st = BELParser.parseStatement(s);
		System.out.println("BEL: " + s);
		BNode bn = processBelStatement(st, npCreator);
		for (BELAnnotation ann : belStatement.getAnnotations()) {
			String annN = ann.getAnnotationDefinition().getName();
			URI annNs = BelRdfVocabulary.getAnnotation(annN);
			npCreator.addNamespace(annN.toLowerCase(), annNs);
			for (String annV : ann.getValues()) {
				URI annUri = new URIImpl(annNs + encodeUrlString(annV));
				npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasAnnotation, annUri);
			}
		}
		return bn;
	}

	private BNode processBelObject(org.openbel.framework.common.model.Statement.Object obj, NanopubCreator npCreator) {
		if (obj.getTerm() != null) {
			return processBelTerm(obj.getTerm(), npCreator);
		} else {
			return processBelStatement(obj.getStatement(), npCreator);
		}
	}

	private BNode processBelTerm(Term term, NanopubCreator npCreator) {
		BNode bn = newBNode();
		npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.term);
		String funcAbbrev = term.getFunctionEnum().getAbbreviation();
		URI funcUri = BelRdfVocabulary.getFunction(funcAbbrev);
		funcUri.toString();  // Raise null pointer exception
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

	private void handleProteinVariantTerm(BNode bn, Term protTerm, NanopubCreator npCreator) {
		Parameter protParam = protTerm.getParameters().get(0);
		URI pUri = getUriFromParam(protParam, npCreator);
		BNode n = newBNode();
		npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasChild, n);
		npCreator.addAssertionStatement(n, RDF.TYPE, BelRdfVocabulary.term);
		npCreator.addAssertionStatement(n, RDF.TYPE, BelRdfVocabulary.getFunction("p"));
		npCreator.addAssertionStatement(n, RDFS.LABEL, new LiteralImpl("p(" + protParam.toBELShortForm() + ")"));
		npCreator.addAssertionStatement(n, BelRdfVocabulary.hasConcept, pUri);
		for (Term varTerm : protTerm.getTerms()) {
			String modAbbrev = varTerm.getFunctionEnum().getAbbreviation();
			if (modAbbrev.equals("pmod")) {
				npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.modifiedProteinAbundance);
				String m = "";
				for (Parameter p : varTerm.getParameters()) {
					m += "," + p.getValue();
				}
				m = m.substring(1);
				URI modUri = BelRdfVocabulary.getModification(m);
				npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasModificationType, modUri);
			} else  {
				npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.proteinVariantAbundance);
				// TODO
			}
		}
	}

	private void handleNormalTerm(BNode bn, Term term, NanopubCreator npCreator) {
		for (Term child : term.getTerms()) {
			BNode ch = processBelTerm(child, npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasChild, ch);
		}
		for (Parameter p : term.getParameters()) {
			URI cUri = getUriFromParam(p, npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasConcept, cUri);
		}
	}

	private URI getUriFromParam(Parameter param, NanopubCreator npCreator) {
		String prefix = param.getNamespace().getPrefix();
		URI ns = BelRdfVocabulary.getNamespace(prefix);
		npCreator.addNamespace(prefix.toLowerCase(), ns);
		return new URIImpl(ns + encodeUrlString(param.getValue()));
	}

	private BNode processBelStatement(Statement statement, NanopubCreator npCreator) {
		BNode bn = newBNode();
		if (statement.getRelationshipType() == null) {
			bn = processBelTerm(statement.getSubject(), npCreator);
		} else {
			npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.statement);
			BNode subj = processBelTerm(statement.getSubject(), npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasSubject, subj);
			URI rel = BelRdfVocabulary.getRel(statement.getRelationshipType().getDisplayValue());
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

	private static String readFile(String path) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
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
