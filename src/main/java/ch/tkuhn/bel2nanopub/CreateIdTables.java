package ch.tkuhn.bel2nanopub;

import java.io.BufferedWriter;
import java.io.FileInputStream;
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

	private String subjString;
	private IdScheme scheme;
	private String id;
	private String label;
	private Map<String,BufferedWriter> writers = new HashMap<String,BufferedWriter>();

	public CreateIdTables() throws Exception {
	}

	public void run() throws Exception {
		for (IdScheme sc : IdScheme.getSchemes()) {
			if (sc.hasDirectMapping()) continue;
			String fileName = "tables/" + sc.getName() + ".txt";
			BufferedWriter w = new BufferedWriter(new FileWriter(fileName));
			writers.put(sc.getName(), w);
		}
		InputStream in = new FileInputStream("downloads/nsmap.ttl");
		RDFParser p = Rio.createParser(RDFFormat.TURTLE);
		p.setRDFHandler(new RDFHandlerBase() {

			@Override
			public void handleStatement(Statement st) throws RDFHandlerException {
				process(st);
			}

		});
		try {
			p.parse(in, baseUri);
		} finally {
			in.close();
			for (BufferedWriter w : writers.values()) {
				w.close();
			}
		}
	}

	private void process(Statement st) {
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
		for (IdScheme sc : IdScheme.getSchemes()) {
			if (sc.hasDirectMapping()) continue;
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

}
