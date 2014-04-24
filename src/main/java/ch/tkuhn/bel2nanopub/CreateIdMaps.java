package ch.tkuhn.bel2nanopub;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

public class CreateIdMaps {

	private static String baseUri = "http://example.org/foo";

	public static void main(String[] args) throws Exception {
		CreateIdMaps obj = new CreateIdMaps();
		obj.run();
	}

	private String subjString;
	private String id;
	private String label;
	private BufferedWriter writer;

	public CreateIdMaps() throws Exception {
	}

	public void run() throws Exception {
		writer = new BufferedWriter(new FileWriter("tables/hgnc.txt"));
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
			writer.close();
		}
	}

	private void process(Statement st) {
		String s = st.getSubject().stringValue();
		if (subjString != null && !s.equals(subjString)) {
			if (id == null || label == null) {
				throw new RuntimeException("Couldn't complete entry for " + subjString);
			} else {
				try {
					writer.write(id + " " + label + "\n");
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				subjString = null;
				id = null;
				label = null;
			}
		}
		if (s.startsWith("http://www.openbel.org/bel/namespace/hgnc-human-genes/")) {
			subjString = s;
		}
		String p = st.getPredicate().stringValue();
		if (p.equals("http://purl.org/dc/terms/identifier")) {
			id = st.getObject().stringValue();
		} else if (p.equals("http://www.w3.org/2004/02/skos/core#prefLabel")) {
			label = st.getObject().stringValue();
		}
	}

}
