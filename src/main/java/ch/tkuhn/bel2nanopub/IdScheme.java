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
	private String mappingType;
	private String directMappingPattern;
	private String belRdfNs;
	private String rdfPrefix;
	private String rdfNs;
	private Set<String> belNsSet = new HashSet<String>();
	private Map<String,String> idMap;

	private IdScheme() {}  // created by GSON

	private void loadMap() {
		if (idMap != null || getMappingType().equals("direct")) return;
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

	public String getMappingType() {
		return mappingType;
	}

	public String getDirectMappingPattern() {
		if (directMappingPattern == null) return "(.*)";
		return directMappingPattern;
	}

	public String getBelRdfNs() {
		return belRdfNs;
	}

	public String getRdfNs() {
		return rdfNs;
	}

	public String getRdfPrefix() {
		if (rdfPrefix == null) return name;
		return rdfPrefix;
	}

	public boolean hasBelNs(String belNs) {
		return belNsSet.contains(belNs);
	}

	public String getId(String belLabel) {
		if (getMappingType().equals("direct")) {
			String pattern = "^" + getDirectMappingPattern() + "$";
			return belLabel.replaceFirst(pattern, "$1");
		} else {
			loadMap();
			return idMap.get(belLabel);
		}
	}

}
