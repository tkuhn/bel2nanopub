package ch.tkuhn.bel2nanopub;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.nanopub.NanopubCreator;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class GeneralVocabulary {

	private GeneralVocabulary() {}  // no instances allowed

	public static URI makeUri(String prefix, String belNs, String belLabel, NanopubCreator npCreator) {
		String uriString;
		if (belNs.equals(HGNC_BELNS_1) || belNs.equals(HGNC_BELNS_2)) {
			npCreator.addNamespace("hgnc", HGNC_NS);
			uriString = HGNC_NS + getHgncId(belLabel);
		} else {
			String ns = belNs.replaceFirst("\\.belns$", "");
			if (!ns.endsWith("/")) ns += "/";
			npCreator.addNamespace(prefix.toLowerCase(), ns);
			uriString = ns + Utils.encodeUrlString(belLabel);
		}
		return new URIImpl(uriString);
	}

	private static final String BELNS_1 = "http://resource.belframework.org/belframework/1.0/namespace/";
	private static final String BELNS_2 = "http://resource.belframework.org/belframework/20131211/namespace/";

	private static final String HGNC_BELNS_1 = BELNS_1 + "hgnc-approved-symbols.belns";
	private static final String HGNC_BELNS_2 = BELNS_2 + "hgnc-human-genes.belns";

	private static final String HGNC_NS = "http://www.genenames.org/cgi-bin/gene_symbol_report?hgnc_id=";

	private static Map<String,String> hgncMap;

	public static String getHgncId(String belLabel) {
		if (hgncMap == null) {
			hgncMap = new HashMap<String,String>();
			try {
				BufferedReader r = new BufferedReader(new FileReader("tables/hgnc.txt"));
				String line;
				while ((line = r.readLine()) != null) {
					int i = line.indexOf(" ");
					hgncMap.put(line.substring(i+1), line.substring(0, i));
				}
				r.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return hgncMap.get(belLabel);
	}

}
