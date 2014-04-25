package ch.tkuhn.bel2nanopub;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nanopub.NanopubCreator;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.google.common.collect.ImmutableList;

public class IdScheme {

	static {
		List<IdScheme> list = new ArrayList<IdScheme>();
		IdScheme sc;

		// HGNC
		sc = new IdScheme("hgnc");
		sc.setDirectMapping(false);
		sc.addBelNs("http://resource.belframework.org/belframework/1.0/namespace/hgnc-approved-symbols.belns");
		sc.addBelNs("http://resource.belframework.org/belframework/20131211/namespace/hgnc-human-genes.belns");
		sc.setBelRdfNs("http://www.openbel.org/bel/namespace/hgnc-human-genes/");
		sc.setRdfNs("http://www.genenames.org/cgi-bin/gene_symbol_report?hgnc_id=");
		list.add(sc);

		// MGI
		sc = new IdScheme("mgi");
		sc.setDirectMapping(false);
		sc.addBelNs("http://resource.belframework.org/belframework/1.0/namespace/mgi-approved-symbols.belns");
		sc.addBelNs("http://resource.belframework.org/belframework/20131211/namespace/mgi-mouse-genes.belns");
		sc.setBelRdfNs("http://www.openbel.org/bel/namespace/mgi-mouse-genes/");
		sc.setRdfNs("http://www.informatics.jax.org/marker/MGI:");
		list.add(sc);

		// Entrez
		sc = new IdScheme("entrez");
		sc.setDirectMapping(true);
		sc.addBelNs("http://resource.belframework.org/belframework/1.0/namespace/entrez-gene-ids-hmr.belns");
		sc.addBelNs("http://resource.belframework.org/belframework/20131211/namespace/entrez-gene-ids.belns");
		sc.setBelRdfNs("http://www.openbel.org/bel/namespace/entrez-gene/");
		sc.setRdfNs("http://www.ncbi.nlm.nih.gov/gene/");
		list.add(sc);

		idSchemes = ImmutableList.copyOf(list);
	}

	private static List<IdScheme> idSchemes;

	public static List<IdScheme> getSchemes() {
		return idSchemes;
	}

	public static URI makeUri(String prefix, String belNs, String belLabel, NanopubCreator npCreator) {
		String uriString = null;
		for (IdScheme sc : getSchemes()) {
			if (sc.hasBelNs(belNs)) {
				npCreator.addNamespace(sc.getName(), sc.getRdfNs());
				uriString = sc.getRdfNs() + sc.getId(belLabel);
				break;
			}
		}
		if (uriString == null) {
			String ns = belNs.replaceFirst("\\.belns$", "");
			if (!ns.endsWith("/")) ns += "/";
			npCreator.addNamespace(prefix.toLowerCase(), ns);
			uriString = ns + Utils.encodeUrlString(belLabel);
		}
		return new URIImpl(uriString);
	}


	private String name;
	private boolean directMapping = false;
	private String belRdfNs;
	private String rdfNs;
	private Set<String> belNsSet = new HashSet<String>();
	private Map<String,String> idMap;

	private IdScheme(String name) {
		this.name = name;
	}

	private void loadMap() {
		if (idMap != null || hasDirectMapping()) return;
		idMap = new HashMap<String,String>();
		try {
			BufferedReader r = new BufferedReader(new FileReader("tables/" + name + ".txt"));
			String line;
			while ((line = r.readLine()) != null) {
				int i = line.indexOf(" ");
				idMap.put(line.substring(i+1), line.substring(0, i));
			}
			r.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	void setDirectMapping(boolean directMapping) {
		this.directMapping = directMapping;
	}

	public boolean hasDirectMapping() {
		return directMapping;
	}

	void setBelRdfNs(String belRdfNs) {
		this.belRdfNs = belRdfNs;
	}

	public String getBelRdfNs() {
		return belRdfNs;
	}

	void setRdfNs(String rdfNs) {
		this.rdfNs = rdfNs;
	}

	public String getRdfNs() {
		return rdfNs;
	}

	void addBelNs(String belNs) {
		belNsSet.add(belNs);
	}

	public boolean hasBelNs(String belNs) {
		return belNsSet.contains(belNs);
	}

	public String getId(String belLabel) {
		if (hasDirectMapping()) {
			return belLabel;
		} else {
			loadMap();
			return idMap.get(belLabel);
		}
	}

}
