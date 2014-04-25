package ch.tkuhn.bel2nanopub;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IdScheme {

	private String name;
	private boolean directMapping = false;
	private String belRdfNs;
	private String rdfPrefix;
	private String rdfNs;
	private Set<String> belNsSet = new HashSet<String>();
	private Map<String,String> idMap;

	private IdScheme() {}  // created by GSON

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
		if (rdfPrefix == null) return name;
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
