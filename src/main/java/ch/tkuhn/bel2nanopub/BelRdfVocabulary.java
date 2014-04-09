package ch.tkuhn.bel2nanopub;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * Namespaces and mappings as defined and used by bel2rdf.
 *
 * @author Tobias Kuhn
 */
public class BelRdfVocabulary {

	private BelRdfVocabulary() {}  // no instances allowed

	public static final URI BELV_NS = new URIImpl("http://www.selventa.com/vocabulary/");

	public static final URI term = new URIImpl(BELV_NS + "Term");
	public static final URI statement = new URIImpl(BELV_NS + "Statement");
	public static final URI hasChild = new URIImpl(BELV_NS + "hasChild");
	public static final URI hasSubject = new URIImpl(BELV_NS + "hasSubject");
	public static final URI hasRelationship = new URIImpl(BELV_NS + "hasRelationship");
	public static final URI hasObject = new URIImpl(BELV_NS + "hasObject");

	private static Map<String,URI> functionMap = new HashMap<String,URI>();

	static {
		functionMap.put("a", new URIImpl(BELV_NS + "Abundance"));
		functionMap.put("g", new URIImpl(BELV_NS + "GeneAbundance"));
		functionMap.put("p", new URIImpl(BELV_NS + "ProteinAbundance"));
		functionMap.put("r", new URIImpl(BELV_NS + "RNAAbundance"));
		functionMap.put("m", new URIImpl(BELV_NS + "microRNAAbundance"));
		functionMap.put("complex", new URIImpl(BELV_NS + "ComplexAbundance"));
		functionMap.put("composite", new URIImpl(BELV_NS + "CompositeAbundance"));
		functionMap.put("bp", new URIImpl(BELV_NS + "BiologicalProcess"));
		functionMap.put("path", new URIImpl(BELV_NS + "Pathology"));
		functionMap.put("rxn", new URIImpl(BELV_NS + "Reaction"));
		functionMap.put("tloc", new URIImpl(BELV_NS + "Translocation"));
		functionMap.put("sec", new URIImpl(BELV_NS + "CellSecretion"));
		functionMap.put("deg", new URIImpl(BELV_NS + "Degradation"));
		functionMap.put("cat", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("chap", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("gtp", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("kin", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("act", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("pep", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("phos", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("ribo", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("tscript", new URIImpl(BELV_NS + "AbundanceActivity"));
		functionMap.put("tport", new URIImpl(BELV_NS + "AbundanceActivity"));
	}

	public static URI getFunction(String abbrev) {
		return functionMap.get(abbrev);
	}

}
