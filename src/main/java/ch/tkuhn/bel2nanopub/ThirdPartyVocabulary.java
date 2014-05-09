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

	//public static final URI bfoHasPart = new URIImpl("http://purl.obolibrary.org/obo/BFO_0000051");
	//public static final URI bfoOccursIn = new URIImpl("http://purl.obolibrary.org/obo/BFO_0000066");

	public static final URI dcTitle = new URIImpl("http://purl.org/dc/elements/1.1/title");
	public static final URI dcRights = new URIImpl("http://purl.org/dc/elements/1.1/rights");
	public static final URI dcDescription = new URIImpl("http://purl.org/dc/elements/1.1/description");
	public static final URI dcLicense = new URIImpl("http://purl.org/dc/terms/license");

	public static final URI pavVersion = new URIImpl("http://purl.org/pav/version");

}
