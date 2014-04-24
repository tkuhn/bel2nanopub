package ch.tkuhn.bel2nanopub;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

}
