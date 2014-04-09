package ch.tkuhn.bel2nanopub;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

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
		System.out.println("INPUT FILE:");
		System.out.println("---");
		System.out.println(belDoc);
		System.out.println("---");
		BELParseResults result = BELParser.parse(belDoc);

		if (!result.getSyntaxErrors().isEmpty()) {
			System.err.println("ERRORS:");
			for (BELParseErrorException ex : result.getSyntaxErrors()) {
				System.err.println(ex.getMessage());
			}
		}

		int bnodeCount = 0;

		for (BELStatementGroup g : result.getDocument().getBelStatementGroups()) {
			for (BELStatement bst : g.getStatements()) {
				String s = bst.getStatementSyntax();
				Statement st = BELParser.parseStatement(s);
				if (st == null) continue;
				System.out.println("BEL: " + s);
				NanopubCreator npCreator = new NanopubCreator("http://www.tkuhn.ch/bel2nanopub/");
				npCreator.addNamespace("belv", BelRdfVocabulary.BELV_NS);
				npCreator.addNamespace("np", "http://www.nanopub.org/nschema#");
				npCreator.addNamespace("rdfs", RDFS.NAMESPACE);
				BNode topBn = new BNodeImpl("" + bnodeCount++);
				npCreator.addAssertionStatement(topBn, RDFS.LABEL, new LiteralImpl(s));
				if (st.getRelationshipType() == null) {
					npCreator.addAssertionStatement(topBn, RDF.TYPE, BelRdfVocabulary.Term);
					Term subj = st.getSubject();
					URI funcUri = BelRdfVocabulary.getFunction(subj.getFunctionEnum().getAbbreviation());
					npCreator.addAssertionStatement(topBn, RDF.TYPE, funcUri);
				} else {
					npCreator.addAssertionStatement(topBn, RDF.TYPE, BelRdfVocabulary.Statement);
				}
				try {
					Nanopub np = TransformNanopub.transform(npCreator.finalizeNanopub());
					System.out.println("NANOPUB:");
					NanopubUtils.writeToStream(np, System.out, RDFFormat.TRIG);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("---");
			}
		}
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
