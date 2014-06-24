package ch.tkuhn.bel2nanopub;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class ThirdPartyVocabulary {

	private ThirdPartyVocabulary() {}  // no instances allowed

	public static final String pubmedNs = "http://www.ncbi.nlm.nih.gov/pubmed/";

	public static final URI provValue = new URIImpl("http://www.w3.org/ns/prov#value");
	public static final URI provWasQuotedFrom = new URIImpl("http://www.w3.org/ns/prov#wasQuotedFrom");
	public static final URI provWasDerivedFrom = new URIImpl("http://www.w3.org/ns/prov#wasDerivedFrom");
	public static final URI provHadPrimarySource = new URIImpl("http://www.w3.org/ns/prov#hadPrimarySource");

	public static final URI oboNs = new URIImpl("http://purl.obolibrary.org/obo/");
	public static final URI bfoHasPart = new URIImpl("http://purl.obolibrary.org/obo/BFO_0000051");
	public static final URI bfoOccursIn = new URIImpl("http://purl.obolibrary.org/obo/BFO_0000066");

	public static final URI pavVersion = new URIImpl("http://purl.org/pav/version");

	public static final URI goIntracellular = new URIImpl("http://amigo.geneontology.org/amigo/term/GO:0005622");
	public static final URI goExtracellularRegion = new URIImpl("http://amigo.geneontology.org/amigo/term/GO:0005576");
	public static final URI goCellSurface = new URIImpl("http://amigo.geneontology.org/amigo/term/GO:0009986");
	public static final URI goProteinComplex = new URIImpl("http://amigo.geneontology.org/amigo/term/GO:0043234");

	public static final URI sioHasAgent = new URIImpl("http://semanticscience.org/resource/SIO_000139");
	public static final URI sioBiochemicalReaction = new URIImpl("http://semanticscience.org/resource/SIO_010036");
	public static final URI sioHasInput = new URIImpl("http://semanticscience.org/resource/SIO_000230");
	public static final URI sioHasOutput = new URIImpl("http://semanticscience.org/resource/SIO_000229");
	public static final URI sioHasAnnotation = new URIImpl("http://semanticscience.org/resource/SIO_000255");
	// ( currently not used:
	public static final URI sioIsTranscribedInto = new URIImpl("http://semanticscience.org/resource/SIO_010080");
	public static final URI sioIsTranslatedInto = new URIImpl("http://semanticscience.org/resource/SIO_010082");
	public static final URI sioIsVariantOf = new URIImpl("http://semanticscience.org/resource/SIO_000272");
	public static final URI sioHasTarget = new URIImpl("http://semanticscience.org/resource/SIO_000292");  // "target" is passive; "agent" is active
	// )

	public static final URI roGeneProductOf = new URIImpl("http://purl.obolibrary.org/obo/RO_0002204");
	// ( currently not used:
	public static final URI roHasInput = new URIImpl("http://purl.obolibrary.org/obo/RO_0002233");
	public static final URI roHasOutput = new URIImpl("http://purl.obolibrary.org/obo/RO_0002234");
	// )

	public static final URI chebiProtein = new URIImpl("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI_36080");
	public static final URI chebiRna = new URIImpl("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI_33697");

	public static final URI soMicroRna = new URIImpl("http://purl.obolibrary.org/obo/SO_0000276");

	public static final URI modProteinModification = new URIImpl("http://www.ebi.ac.uk/ontology-lookup/?termId=MOD:00000");

}
