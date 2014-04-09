package ch.tkuhn.bel2nanopub;

import java.io.IOException;
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
import org.openbel.bel.model.BELParseErrorException;
import org.openbel.bel.model.BELStatement;
import org.openbel.bel.model.BELStatementGroup;
import org.openbel.framework.common.bel.parser.BELParseResults;
import org.openbel.framework.common.bel.parser.BELParser;
import org.openbel.framework.common.model.Statement;
import org.openbel.framework.common.model.Term;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
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
		npCreator.addAssertionStatement(bn, RDFS.LABEL, new LiteralImpl(s));
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
		URI funcUri = BelRdfVocabulary.getFunction(term.getFunctionEnum().getAbbreviation());
		npCreator.addAssertionStatement(bn, RDF.TYPE, funcUri);
		// TODO ...
		return bn;
	}

	private BNode processBelStatement(Statement statement, NanopubCreator npCreator) {
		BNode bn = newBNode();
		if (statement.getRelationshipType() == null) {
			bn = processBelTerm(statement.getSubject(), npCreator);
		} else {
			npCreator.addAssertionStatement(bn, RDF.TYPE, BelRdfVocabulary.statement);
			BNode subj = processBelTerm(statement.getSubject(), npCreator);
			npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasSubject, subj);
			// TODO hasRelationship
			if (statement.getObject() != null) {
				BNode obj = processBelObject(statement.getObject(), npCreator);
				npCreator.addAssertionStatement(bn, BelRdfVocabulary.hasObject, obj);
			}
			// TODO ...
		}
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

}
