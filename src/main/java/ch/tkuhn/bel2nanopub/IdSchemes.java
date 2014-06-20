package ch.tkuhn.bel2nanopub;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.nanopub.NanopubCreator;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IdSchemes {

	private static IdSchemes singleton;

	static {
		try {
			Reader reader = new InputStreamReader(Bel2Nanopub.class.getResourceAsStream("/idschemes.json"), "UTF-8");
			Gson gson = new GsonBuilder().create();
			singleton = gson.fromJson(reader, IdSchemes.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<IdScheme> getSchemes() {
		return singleton.schemes;
	}

	public static URI makeUri(String schemeNameOrBelNs, String belLabel, NanopubCreator npCreator) {
		return makeUri(schemeNameOrBelNs, belLabel, npCreator, null);
	}

	public static URI makeUri(String schemeNameOrBelNs, String belLabel, NanopubCreator npCreator, String prefix) {
		if (belLabel.startsWith("\"") && belLabel.endsWith("\"") && belLabel.length() > 1) {
			belLabel = belLabel.substring(1, belLabel.length()-1);
		}
		String uriString = null;
		IdScheme sc = getScheme(schemeNameOrBelNs);
		if (sc != null) {
			String n = sc.getRdfNs();
			if (n.contains(" ")) {
				n = n.substring(0, n.indexOf(" "));
			}
			String id = sc.getId(belLabel);
			if (id == null) {
				return null;
			} else {
				if (sc.getRdfNs().contains(" ")) {
					uriString = sc.getRdfNs().replaceAll(" ", id);
				} else {
					uriString = sc.getRdfNs() + id;
				}
			}
			npCreator.addNamespace(sc.getRdfPrefix(), sc.getRdfNs());
		}
		String ns = schemeNameOrBelNs;
		if (uriString == null && ns.contains("://")) {
			ns = ns.replaceFirst("\\.bel(ns|anno)$", "");
			if (!ns.endsWith("/")) ns += "/";
			uriString = ns + Utils.encodeUrlString(belLabel);
			if (prefix != null) {
				npCreator.addNamespace(prefix.toLowerCase(), ns);
			}
		}
		return new URIImpl(uriString);
	}

	public static IdScheme getScheme(String schemeNameOrBelNs) {
		for (IdScheme sc : getSchemes()) {
			if (sc.getName().equals(schemeNameOrBelNs) || sc.hasBelNs(schemeNameOrBelNs)) {
				return sc;
			}
		}
		return null;
	}

	private IdSchemes() {}  // created by GSON

	private List<IdScheme> schemes;

}
