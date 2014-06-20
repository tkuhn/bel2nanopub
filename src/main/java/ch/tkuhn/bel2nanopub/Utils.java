package ch.tkuhn.bel2nanopub;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;

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

	public static String readFile(File file) {
		try {
			byte[] encoded = Files.readAllBytes(file.toPath());
			return Charset.forName("UTF-8").decode(ByteBuffer.wrap(encoded)).toString();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
