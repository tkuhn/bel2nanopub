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

	public static final URI BELR_NS = new URIImpl("http://www.selventa.com/bel/");
	public static final URI BELV_NS = new URIImpl("http://www.selventa.com/vocabulary/");

	public static final URI term = new URIImpl(BELV_NS + "Term");
	public static final URI statement = new URIImpl(BELV_NS + "Statement");
	public static final URI hasChild = new URIImpl(BELV_NS + "hasChild");
	public static final URI hasConcept = new URIImpl(BELV_NS + "hasConcept");
	public static final URI hasSubject = new URIImpl(BELV_NS + "hasSubject");
	public static final URI hasRelationship = new URIImpl(BELV_NS + "hasRelationship");
	public static final URI hasObject = new URIImpl(BELV_NS + "hasObject");
	public static final URI hasActivityType = new URIImpl(BELV_NS + "hasActivityType");

	private static Map<String,URI> namespaceMap = new HashMap<String,URI>();

	static {
		namespaceMap.put("EGID", new URIImpl(BELV_NS + "namespace/entrez-gene-ids/"));
		namespaceMap.put("HGNC", new URIImpl(BELV_NS + "namespace/hgnc-approved-symbols/"));
		namespaceMap.put("MGI", new URIImpl(BELV_NS + "namespace/mgi-approved-symbols/"));
		namespaceMap.put("RGD", new URIImpl(BELV_NS + "namespace/rgd-approved-symbols/"));
		namespaceMap.put("AFFY", new URIImpl(BELV_NS + "namespace/affy-probeset-ids/"));
		namespaceMap.put("SCOM", new URIImpl(BELV_NS + "namespace/selventa-named-complexes/"));
		namespaceMap.put("MESHCL", new URIImpl(BELV_NS + "namespace/mesh-cellular-locations/"));
		namespaceMap.put("SFAM", new URIImpl(BELV_NS + "namespace/selventa-protein-families/"));
		namespaceMap.put("CHEBI", new URIImpl(BELV_NS + "namespace/chebi-names/"));
		namespaceMap.put("NCH", new URIImpl(BELV_NS + "namespace/selventa-named-complexes-human/"));
		namespaceMap.put("NCM", new URIImpl(BELV_NS + "namespace/selventa-named-complexes-mouse/"));
		namespaceMap.put("NCR", new URIImpl(BELV_NS + "namespace/selventa-named-complexes-rat/"));
		namespaceMap.put("PFH", new URIImpl(BELV_NS + "namespace/selventa-protein-families-human/"));
		namespaceMap.put("PFM", new URIImpl(BELV_NS + "namespace/selventa-protein-families-mouse/"));
		namespaceMap.put("PFR", new URIImpl(BELV_NS + "namespace/selventa-protein-families-rat/"));
		namespaceMap.put("GO", new URIImpl(BELV_NS + "namespace/go/"));
		namespaceMap.put("MESHPP", new URIImpl(BELV_NS + "namespace/mesh-processes/"));
		namespaceMap.put("MESHD", new URIImpl(BELV_NS + "namespace/mesh-diseases/"));
		namespaceMap.put("SCHEM", new URIImpl(BELV_NS + "namespace/selventa-legacy-chemical-names/"));
		namespaceMap.put("SDIS", new URIImpl(BELV_NS + "namespace/selventa-legacy-diseases/"));
		namespaceMap.put("GOBP", new URIImpl(BELV_NS + "namespace/go-biological-processes-names/"));
		namespaceMap.put("GOCCID", new URIImpl(BELV_NS + "namespace/go-cellular-component-ids/"));
		namespaceMap.put("GOCC", new URIImpl(BELV_NS + "namespace/go-cellular-component-names/"));
	}

	public static URI getNamespace(String ns) {
		return namespaceMap.get(ns);
	}

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

		// additional (not found in bel2rdf):
		functionMap.put("pmod", new URIImpl(BELV_NS + "ProteinModification"));
		functionMap.put("sub", new URIImpl(BELV_NS + "Substitution"));
	}

	public static URI getFunction(String abbrev) {
		return functionMap.get(abbrev);
	}

	private static Map<String,String> relNormalizeMap = new HashMap<String,String>();

	static {
		relNormalizeMap.put("analogous", "analogous");
		relNormalizeMap.put("association", "association");
		relNormalizeMap.put("--", "association");
		relNormalizeMap.put("biomarkerFor", "biomarkerFor");
		relNormalizeMap.put("causesNoChange", "causesNoChange");
		relNormalizeMap.put("decreases", "decreases");
		relNormalizeMap.put("-|", "decreases");
		relNormalizeMap.put("directlyDecreases", "directlyDecreases");
		relNormalizeMap.put("=|", "directlyDecreases");
		relNormalizeMap.put("directlyIncreases", "directlyIncreases");
		relNormalizeMap.put("=>", "directlyIncreases");
		relNormalizeMap.put("hasComponent", "hasComponent");
		relNormalizeMap.put("hasComponents", "hasComponents");
		relNormalizeMap.put("hasMember", "hasMember");
		relNormalizeMap.put("hasMembers", "hasMembers");
		relNormalizeMap.put("hasModification", "hasModification");
		relNormalizeMap.put("hasProduct", "hasProduct");
		relNormalizeMap.put("hasVariant", "hasVariant");
		relNormalizeMap.put("includes", "includes");
		relNormalizeMap.put("increases", "increases");
		relNormalizeMap.put("->", "increases");
		relNormalizeMap.put("isA", "isA");
		relNormalizeMap.put("negativeCorrelation", "negativeCorrelation");
		relNormalizeMap.put("orthologous", "orthologous");
		relNormalizeMap.put("positiveCorrelation", "positiveCorrelation");
		relNormalizeMap.put("prognosticBiomarkerFor", "prognosticBiomarkerFor");
		relNormalizeMap.put("rateLimitingStepOf", "rateLimitingStepOf");
		relNormalizeMap.put("reactantIn", "reactantIn");
		relNormalizeMap.put("subProcessOf", "subProcessOf");
		relNormalizeMap.put("transcribedTo", "transcribedTo");
		relNormalizeMap.put(":>", "transcribedTo");
		relNormalizeMap.put(">>", "translatedTo");
		relNormalizeMap.put("translatedTo", "translatedTo");
		relNormalizeMap.put("translocates", "translocates");
	}

	public static String getNormalizedRel(String rel) {
		return relNormalizeMap.get(rel);
	}

	private static Map<String,URI> relMap = new HashMap<String,URI>();

	static {
		relMap.put("association", new URIImpl(BELV_NS + "correlativeRelationship"));
		relMap.put("biomarkerFor", new URIImpl(BELV_NS + "biomarkerFor"));
		relMap.put("causesNoChange", new URIImpl(BELV_NS + "causesNoChange"));
		relMap.put("decreases", new URIImpl(BELV_NS + "decreases"));
		relMap.put("directlyDecreases", new URIImpl(BELV_NS + "directlyDecreases"));
		relMap.put("directlyIncreases", new URIImpl(BELV_NS + "directlyIncreases"));
		relMap.put("hasComponent", new URIImpl(BELV_NS + "hasComponent"));
		relMap.put("hasMember", new URIImpl(BELV_NS + "hasMember"));
		relMap.put("increases", new URIImpl(BELV_NS + "increases"));
		relMap.put("isA", new URIImpl(BELV_NS + "isA"));
		relMap.put("negativeCorrelation", new URIImpl(BELV_NS + "negativeCorrelation"));
		relMap.put("positiveCorrelation", new URIImpl(BELV_NS + "positiveCorrelation"));
		relMap.put("prognosticBiomarkerFor", new URIImpl(BELV_NS + "prognosticBiomarkerFor"));
		relMap.put("rateLimitingStepOf", new URIImpl(BELV_NS + "rateLimitingStepOf"));
		relMap.put("subProcessOf", new URIImpl(BELV_NS + "subProcessOf"));
	}

	public static URI getRel(String rel) {
		return relMap.get(relNormalizeMap.get(rel));
	}

	private static Map<String,URI> activityMap = new HashMap<String,URI>();

	static {
		activityMap.put("cat", new URIImpl(BELV_NS + "Catalytic"));
		activityMap.put("chap", new URIImpl(BELV_NS + "Chaperone"));
		activityMap.put("gtp", new URIImpl(BELV_NS + "GtpBound"));
		activityMap.put("kin", new URIImpl(BELV_NS + "Kinase"));
		activityMap.put("act", new URIImpl(BELV_NS + "Activity"));
		activityMap.put("pep", new URIImpl(BELV_NS + "Peptidase"));
		activityMap.put("phos", new URIImpl(BELV_NS + "Phosphatase"));
		activityMap.put("ribo", new URIImpl(BELV_NS + "Ribosylase"));
		activityMap.put("tscript", new URIImpl(BELV_NS + "Transcription"));
		activityMap.put("tport", new URIImpl(BELV_NS + "Transport"));
	}

	public static URI getActivity(String activity) {
		return activityMap.get(activity);
	}

}
