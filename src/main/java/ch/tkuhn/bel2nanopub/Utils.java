package ch.tkuhn.bel2nanopub;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openbel.framework.common.model.Term;

public class Utils {

	private Utils() {}  // no instances allowed

	public static String encodeUrlString(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String getFunctionAbbrev(Term term) {
		String funcAbbrev = term.getFunctionEnum().getAbbreviation();
		if (funcAbbrev == null) funcAbbrev = term.getFunctionEnum().getDisplayValue();
		return funcAbbrev;
	}

}
