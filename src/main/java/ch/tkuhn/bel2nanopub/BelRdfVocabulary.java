package ch.tkuhn.bel2nanopub;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class BelRdfVocabulary {

	private BelRdfVocabulary() {}  // no instances allowed

	public static final URI BELV_NS = new URIImpl("http://www.selventa.com/vocabulary/");

	public static final URI Term = new URIImpl(BELV_NS + "Term");
	public static final URI Statement = new URIImpl(BELV_NS + "Statement");

}
