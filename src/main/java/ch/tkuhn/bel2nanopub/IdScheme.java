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

		// ChEBI (indirect)
		sc = new IdScheme("chebi");
		sc.setDirectMapping(false);
		sc.addBelNs("http://resource.belframework.org/belframework/1.0/namespace/chebi-names.belns");
		sc.addBelNs("http://resource.belframework.org/belframework/20131211/namespace/chebi.belns");
		sc.setBelRdfNs("http://www.openbel.org/bel/namespace/chebi/");
		sc.setRdfNs("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=");
		list.add(sc);

		// ChEBI (direct)
		sc = new IdScheme("chebi-ids");
		sc.setDirectMapping(true);
		sc.addBelNs("http://resource.belframework.org/belframework/1.0/namespace/chebi-ids.belns");
		sc.addBelNs("http://resource.belframework.org/belframework/20131211/namespace/chebi-ids.belns");
		sc.setBelRdfNs("http://www.openbel.org/bel/namespace/chebi/");
		sc.setRdfPrefix("chebi");
		sc.setRdfNs("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=");
		list.add(sc);

		idSchemes = ImmutableList.copyOf(list);
	}

	private static List<IdScheme> idSchemes;

	public static List<IdScheme> getSchemes() {
		return idSchemes;
	}

	public static URI makeUri(String prefix, String belNs, String belLabel, NanopubCreator npCreator) {
		if (belLabel.startsWith("\"") && belLabel.endsWith("\"") && belLabel.length() > 1) {
			belLabel = belLabel.substring(1, belLabel.length()-1);
		}
		String uriString = null;
		for (IdScheme sc : getSchemes()) {
			if (sc.hasBelNs(belNs)) {
				npCreator.addNamespace(sc.getRdfPrefix(), sc.getRdfNs());
				String id = sc.getId(belLabel);
				if (id == null) {
					uriString = null;
				} else {
					uriString = sc.getRdfNs() + id;
				}
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
	private String rdfPrefix;
	private String rdfNs;
	private Set<String> belNsSet = new HashSet<String>();
	private Map<String,String> idMap;

	private IdScheme(String name) {
		this.name = name;
		this.rdfPrefix = name;
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

	void setRdfPrefix(String rdfPrefix) {
		this.rdfPrefix = rdfPrefix;
	}

	public String getRdfPrefix() {
		return rdfPrefix;
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
