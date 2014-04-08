package ch.tkuhn.bel2nanopub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openbel.bel.model.BELParseErrorException;
import org.openbel.bel.model.BELStatement;
import org.openbel.bel.model.BELStatementGroup;
import org.openbel.framework.common.bel.parser.BELParseResults;
import org.openbel.framework.common.bel.parser.BELParser;
import org.openbel.framework.common.model.Statement;

public class Bel2Nanopub {

	public static void main(String[] args) {
		String belDoc = getResourceAsString("/examples/pubmed9202001.bel");
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
				System.out.println("STATEMENT: " + s);
				Statement st = BELParser.parseStatement(s);
				if (st != null) System.out.println("PARSED SUCCESSFULLY.");
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
