package ch.tkuhn.bel2nanopub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

public class CreateIdTables {

	private static String baseUri = "http://example.org/foo";

	public static void main(String[] args) throws Exception {
		CreateIdTables obj = new CreateIdTables();
		obj.run();
	}

	private Map<String,BufferedWriter> writers = new HashMap<String,BufferedWriter>();
	private Map<String,String> meshTreeMap = new HashMap<String,String>();

	public CreateIdTables() throws Exception {
	}

	public void run() throws Exception {
		for (IdScheme sc : IdSchemes.getSchemes()) {
			if (sc.getMappingType().equals("direct") || sc.getMappingType().equals("manual")) {
				continue;
			}
			BufferedWriter w = new BufferedWriter(new FileWriter(sc.getTableFileName()));
			writers.put(sc.getName(), w);
		}
		try {
			processNsMap();
			loadMeshTreeMap();
			processAnnMaps();
		} finally {
			for (BufferedWriter w : writers.values()) {
				w.close();
			}
		}
	}

	private void processNsMap() throws Exception {
		InputStream in = new FileInputStream("downloads/nsmap.ttl");
		RDFParser p = Rio.createParser(RDFFormat.TURTLE);
		p.setRDFHandler(new RDFHandlerBase() {

			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				processNsMapStatement(st);
			}

		});
		try {
			p.parse(in, baseUri);
		} finally {
			in.close();
		}
	}

	private String subjString;
	private IdScheme scheme;
	private String id;
	private String label;

	private void processNsMapStatement(Statement st) {
		String s = st.getSubject().stringValue();
		if (subjString != null && !s.equals(subjString)) {
			if (id == null || label == null) {
				throw new RuntimeException("Couldn't complete entry for " + subjString);
			} else {
				try {
					writers.get(scheme.getName()).write(id + " " + label + "\n");
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				subjString = null;
				scheme = null;
				id = null;
				label = null;
			}
		}
		for (IdScheme sc : IdSchemes.getSchemes()) {
			if (!sc.getMappingType().equals("belns")) continue;
			if (s.startsWith(sc.getBelRdfNs())) {
				subjString = s;
				scheme = sc;
				break;
			}
		}
		String p = st.getPredicate().stringValue();
		if (p.equals("http://purl.org/dc/terms/identifier")) {
			id = st.getObject().stringValue();
		} else if (p.equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
			label = st.getObject().stringValue();
		}
	}

	private void loadMeshTreeMap() throws Exception {
		BufferedReader r = new BufferedReader(new FileReader("downloads/meshtreemap.csv"));
		try {
			String line;
			while ((line = r.readLine()) != null) {
				line = line.trim();
				String id = line.replaceFirst("^\"(.*)\",\"(.*)\"$", "$1");
				String tree = line.replaceFirst("^\"(.*)\",\"(.*)\"$", "$2");
				meshTreeMap.put(tree, id);
			}
		} finally {
			r.close();
		}
	}

	private void processAnnMaps() throws Exception {
		for (IdScheme sc : IdSchemes.getSchemes()) {
			if (!sc.getMappingType().startsWith("belanno")) continue;
			for (String belNs : sc.getBelNsSet()) {
				String fileName = belNs.replaceFirst("^http://", "downloads/");
				BufferedReader r = new BufferedReader(new FileReader(fileName));
				try {
					boolean started = false;
					String line;
					while ((line = r.readLine()) != null) {
						line = line.trim();
						if (line.equals("[Values]")) {
							started = true;
							continue;
						}
						if (!started || line.isEmpty()) continue;
						if (!line.contains("|")) {
							throw new RuntimeException("Invalid line in belanno file for " + belNs + ": " + line);
						}
						int i = line.indexOf("|");
						String label = line.substring(0, i);
						String id = line.substring(i + 1);
						id = sc.applyDirectMappingPattern(id);
						if (sc.getMappingType().equals("belanno-mesh")) {
							id = meshTreeMap.get(id).toString();
						}
						writers.get(sc.getName()).write(id + " " + label + "\n");
					}
				} finally {
					r.close();
				}
			}
		}
	}

}
