package ch.tkuhn.bel2nanopub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nanopub.MultiNanopubRdfHandler;
import org.nanopub.Nanopub;
import org.nanopub.NanopubUtils;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class FilterNanopubs {

	@com.beust.jcommander.Parameter(description = "input-nanopub-files", required = true)
	private List<File> inputFiles = new ArrayList<File>();

	@com.beust.jcommander.Parameter(names = "-s", description = "Search for this URI (filter out everything else)", required = true)
	private String searchUri;

	public static void main(String[] args) throws Exception {
		FilterNanopubs obj = new FilterNanopubs();
		JCommander jc = new JCommander(obj);
		try {
			jc.parse(args);
		} catch (ParameterException ex) {
			jc.usage();
			System.exit(1);
		}
		try {
			obj.run();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			System.exit(1);
		}
		System.err.println("TOTAL NANOPUB COUNT: " + obj.npTotal);
		System.err.println("OUTPUT NANOPUB COUNT: " + obj.npOutput);
	}

	private int npTotal = 0;
	private int npOutput = 0;

	public void run() throws Exception {
		final URI sUri = new URIImpl(searchUri);
		for (File f : inputFiles) {
			RDFFormat format = RDFFormat.forFileName(f.getName(), RDFFormat.TRIG);
			MultiNanopubRdfHandler.process(format, f, new MultiNanopubRdfHandler.NanopubHandler() {
				
				@Override
				public void handleNanopub(Nanopub np) {
					npTotal++;
					for (Statement st : np.getAssertion()) {
						if (!sUri.equals(st.getSubject()) && !sUri.equals(st.getPredicate()) && !sUri.equals(st.getObject())) {
							continue;
						}
						try {
							NanopubUtils.writeToStream(np, System.out, RDFFormat.TRIG);
							System.out.print("\n\n");
							npOutput++;
							break;
						} catch (Exception ex) {
							throw new RuntimeException(ex);
						}
					}
				}

			});
		}
	}

}
