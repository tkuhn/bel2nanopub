package ch.tkuhn.bel2nanopub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;

public class Bel2Nanopub {

	public static void main(String[] args) {
		String belDoc = getResourceAsString("/examples/pubmed14734561.bel");
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

		for (BELStatementGroup g : result.getDocument().getBelStatementGroups()) {
			for (BELStatement bst : g.getStatements()) {
				String s = bst.getStatementSyntax();
				Statement st = BELParser.parseStatement(s);
				if (st == null) continue;
				System.out.println("BEL: " + s);
				if (st.getRelationshipType() == null) {
					NanopubCreator npCreator = new NanopubCreator("http://www.tkuhn.ch/bel2nanopub/");
					URI termUri = new URIImpl("http://www.tkuhn.ch/bel2nanopub/Term");
					npCreator.addNamespace("belv", BelRdfVocabulary.BELV_NS);
					npCreator.addNamespace("np", "http://www.nanopub.org/nschema#");
					npCreator.addAssertionStatement(termUri, RDF.TYPE, BelRdfVocabulary.Term);
					try {
						Nanopub np = TransformNanopub.transform(npCreator.finalizeNanopub());
						System.out.println("NANOPUB:");
						NanopubUtils.writeToStream(np, System.out, RDFFormat.TRIG);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					// TODO
				}
				System.out.println("---");
			}
		}
	}

	private static String getResourceAsString(String resourceName) {
		try {
			StringBuffer sb = new StringBuffer();
			InputStream is = Bel2Nanopub.class.getResourceAsStream(resourceName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			for (int c = br.read(); c != -1; c = br.read()) {
				sb.append((char) c);
			}
			return sb.toString();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
