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

	//public static final URI term = new URIImpl(BELV_NS + "Term");
	//public static final URI statement = new URIImpl(BELV_NS + "Statement");
	//public static final URI hasChild = new URIImpl(BELV_NS + "hasChild");
	public static final URI hasConcept = new URIImpl(BELV_NS + "hasConcept");
	//public static final URI hasSubject = new URIImpl(BELV_NS + "hasSubject");
	//public static final URI hasRelationship = new URIImpl(BELV_NS + "hasRelationship");
	//public static final URI hasObject = new URIImpl(BELV_NS + "hasObject");
	//public static final URI hasActivityType = new URIImpl(BELV_NS + "hasActivityType");
	public static final URI hasAnnotation = new URIImpl(BELV_NS + "hasAnnotation");
	//public static final URI hasCitation = new URIImpl(BELV_NS + "hasCitation");
	//public static final URI hasEvidenceText = new URIImpl(BELV_NS + "hasEvidenceText");

	// ---
	// Made up URIs, not used by official BEL tools:
	public static final URI hasSubstitution = new URIImpl(BELV_NS + "hasSubstitution");
	public static final URI hasModification = new URIImpl(BELV_NS + "hasModification");
	public static final URI activityOf = new URIImpl(BELV_NS + "activityOf");
	// ---

	//public static final URI abundanceActivity = new URIImpl(BELV_NS + "AbundanceActivity");
	public static final URI proteinVariantAbundance = new URIImpl(BELV_NS + "ProteinVariantAbundance");
	public static final URI modifiedProteinAbundance = new URIImpl(BELV_NS + "ModifiedProteinAbundance");
	public static final URI hasModificationType = new URIImpl(BELV_NS + "hasModificationType");


	private static Map<String,URI> abundanceFunctionMap = new HashMap<String,URI>();

	static {
		abundanceFunctionMap.put("a", new URIImpl(BELV_NS + "Abundance"));
		abundanceFunctionMap.put("g", new URIImpl(BELV_NS + "GeneAbundance"));
		abundanceFunctionMap.put("p", new URIImpl(BELV_NS + "ProteinAbundance"));
		abundanceFunctionMap.put("r", new URIImpl(BELV_NS + "RNAAbundance"));
		abundanceFunctionMap.put("m", new URIImpl(BELV_NS + "microRNAAbundance"));
		abundanceFunctionMap.put("complex", new URIImpl(BELV_NS + "ComplexAbundance"));
		abundanceFunctionMap.put("composite", new URIImpl(BELV_NS + "CompositeAbundance"));
//		functionMap.put("bp", new URIImpl(BELV_NS + "BiologicalProcess"));
//		functionMap.put("path", new URIImpl(BELV_NS + "Pathology"));

		// TODO remove these from here:
		abundanceFunctionMap.put("reactants", new URIImpl(BELV_NS + "ReactionReactants"));  // (Made up URI, not used by official BEL tools)
		abundanceFunctionMap.put("products", new URIImpl(BELV_NS + "ReactionProducts"));  // (Made up URI, not used by official BEL tools)
		abundanceFunctionMap.put("list", new URIImpl(BELV_NS + "List"));  // (Made up URI, not used by official BEL tools)
	}

	public static URI getAbundanceFunction(String abbrev) {
		return abundanceFunctionMap.get(abbrev);
	}


	private static Map<String,URI> transformFunctionMap = new HashMap<String,URI>();

	static {
		transformFunctionMap.put("rxn", new URIImpl(BELV_NS + "Reaction"));
		transformFunctionMap.put("tloc", new URIImpl(BELV_NS + "Translocation"));
		transformFunctionMap.put("sec", new URIImpl(BELV_NS + "CellSecretion"));
		transformFunctionMap.put("deg", new URIImpl(BELV_NS + "Degradation"));
		transformFunctionMap.put("surf", new URIImpl(BELV_NS + "CellSurfaceExpression"));  // (Made up URI, not used by official BEL tools)
	}

	public static URI getTransformFunction(String abbrev) {
		return transformFunctionMap.get(abbrev);
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

		// Made up URIs, not used by official BEL tools:
		relMap.put("transcribedTo", new URIImpl(BELV_NS + "transcribedTo"));
		relMap.put("translatedTo", new URIImpl(BELV_NS + "translatedTo"));
		relMap.put("translocates", new URIImpl(BELV_NS + "translocates"));
		relMap.put("hasMembers", new URIImpl(BELV_NS + "hasMembers"));
		relMap.put("hasComponents", new URIImpl(BELV_NS + "hasComponents"));
	}

	public static URI getRel(String rel) {
		return relMap.get(relNormalizeMap.get(rel));
	}

	private static Map<String,URI> activityMap = new HashMap<String,URI>();

	static {
		// Added "...Activity":
		activityMap.put("cat", new URIImpl(BELV_NS + "CatalyticActivity"));
		activityMap.put("chap", new URIImpl(BELV_NS + "ChaperoneActivity"));
		activityMap.put("gtp", new URIImpl(BELV_NS + "GtpBoundActivity"));
		activityMap.put("kin", new URIImpl(BELV_NS + "KinaseActivity"));
		activityMap.put("act", new URIImpl(BELV_NS + "MolecularActivity"));
		activityMap.put("pep", new URIImpl(BELV_NS + "PeptidaseActivity"));
		activityMap.put("phos", new URIImpl(BELV_NS + "PhosphataseActivity"));
		activityMap.put("ribo", new URIImpl(BELV_NS + "RibosylaseActivity"));
		activityMap.put("tscript", new URIImpl(BELV_NS + "TranscriptionActivity"));
		activityMap.put("tport", new URIImpl(BELV_NS + "TransportActivity"));
	}

	public static URI getActivity(String activity) {
		return activityMap.get(activity);
	}

//	private static Map<String,URI> modtypeMap = new HashMap<String,URI>();
//
//	static {
//		modtypeMap.put("A", new URIImpl(BELV_NS + "Acetylation"));
//		modtypeMap.put("F", new URIImpl(BELV_NS + "Farnesylation"));
//		modtypeMap.put("G", new URIImpl(BELV_NS + "Glycosylation"));
//		modtypeMap.put("H", new URIImpl(BELV_NS + "Hydroxylation"));
//		modtypeMap.put("M", new URIImpl(BELV_NS + "Methylation"));
//		modtypeMap.put("P", new URIImpl(BELV_NS + "Phosphorylation"));
//		modtypeMap.put("R", new URIImpl(BELV_NS + "Ribosylation"));
//		modtypeMap.put("S", new URIImpl(BELV_NS + "Sumoylation"));
//		modtypeMap.put("U", new URIImpl(BELV_NS + "Ubiquitination"));
//	}
//
//	public static URI getModificationType(String modtype) {
//		return modtypeMap.get(modtype);
//	}

	private static Map<String,String> variantNormalizeMap = new HashMap<String,String>();

	static {
		variantNormalizeMap.put("fus", "fusion");
		variantNormalizeMap.put("fusion", "fusion");
		variantNormalizeMap.put("sub", "substitution");
		variantNormalizeMap.put("substitution", "substitution");
		variantNormalizeMap.put("trunc", "truncation");
		variantNormalizeMap.put("truncation", "truncation");
	}

	public static String getNormalizedVariant(String rel) {
		return variantNormalizeMap.get(rel);
	}

}
