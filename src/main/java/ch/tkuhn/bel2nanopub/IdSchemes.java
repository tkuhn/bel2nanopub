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

	public static URI makeUri(String prefix, String belNs, String belLabel, NanopubCreator npCreator) throws Bel2NanopubException {
		if (belLabel.startsWith("\"") && belLabel.endsWith("\"") && belLabel.length() > 1) {
			belLabel = belLabel.substring(1, belLabel.length()-1);
		}
		String uriString = null;
		for (IdScheme sc : getSchemes()) {
			if (sc.hasBelNs(belNs)) {
				npCreator.addNamespace(sc.getRdfPrefix(), sc.getRdfNs());
				String id = sc.getId(belLabel);
				if (id == null) {
					throw new Bel2NanopubException("Cannot resolve entity '" + belLabel + "' in namespace '" + belNs + "'");
				} else {
					uriString = sc.getRdfNs() + id;
				}
				break;
			}
		}
		if (uriString == null) {
			String ns = belNs.replaceFirst("\\.bel(ns|anno)$", "");
			if (!ns.endsWith("/")) ns += "/";
			npCreator.addNamespace(prefix.toLowerCase(), ns);
			uriString = ns + Utils.encodeUrlString(belLabel);
		}
		return new URIImpl(uriString);
	}

	private IdSchemes() {}  // created by GSON

	private List<IdScheme> schemes;

}
