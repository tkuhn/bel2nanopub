package ch.tkuhn.bel2nanopub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openbel.bel.model.BELParseErrorException;
import org.openbel.framework.common.bel.parser.BELParseResults;
import org.openbel.framework.common.bel.parser.BELParser;

public class Bel2Nanopub {

	public static void main(String[] args) {
		String belDoc = getResourceAsString("/examples/pubmed9202001.bel");
		System.out.println(belDoc);
		System.out.println("---");
		BELParseResults result = BELParser.parse(belDoc);
		for (BELParseErrorException ex : result.getSyntaxErrors()) {
			System.err.println(ex.getMessage());
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
