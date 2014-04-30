BEL2nanopub
===========

Transforming BEL documents into nanopublications.

*Work in progress...*


Dependencies
------------

Maven has to be installed.

Installation of OpenBEL:

    $ git clone git@github.com:OpenBEL/openbel-framework.git
    $ cd openbel-framework/org.openbel.framework.common
    $ mvn install

Installation of latest snapshot of nanopub-java:

    $ git clone git@github.com:Nanopublication/nanopub-java.git
    $ cd nanopub-java
    $ mvn install

Installation of latest snapshot of trustyuri-java:

    $ git clone git@github.com:trustyuri/trustyuri-java.git
    $ cd trustyuri-java
    $ mvn install


Mapping
-------

Below the mapping to RDF and the differences to the mapping by bel.rb are
described. 


### Reification

Original BEL document:

    DEFINE ANNOTATION CellStructure AS URL "http://resource.belframework.org/belframework/1.0/annotation/mesh-cell-structure.belanno"
    SET CellStructure = "Cell Nucleus"
    p(HGNC:XRCC5) => p(HGNC:WRN)

RDF as produced by bel.rb:

    bel:p_HGNC_XRCC5_directlyIncreases_p_HGNC_WRN rdf:type belv:Statement ;
        belv:hasSubject bel:p_HGNC_XRCC5 ;
        belv:hasRelationship "directlyIncreases" ;
        belv:hasObject bel:p_HGNC_WRN ;
        rdfs:label "p(HGNC:XRCC5) -> p(HGNC:WRN)" ;
        belv:hasEvidence _:1 .
    _:1 belv:hasAnnotation <http://www.openbel.org/bel/annotation/cell-structure/Cell%20Nucleus> .

In nanopubs using standard reification:

    node:1 a rdf:Statement ;
        rdf:subject node:2 ;
        rdf:predicate belv:directlyIncreases ;
        rdf:object node:3 ;
        belv:hasAnnotation <http://purl.bioontology.org/ontology/MSH/D002467> .


### Citation/Evidence

Original BEL document:

    SET Citation = {"PubMed", "Some Journal 2004 Apr 1 234(5)","12345678"}
    SET Evidence = "Some quoted evidence text."

RDF as produced by bel.rb:

    bel:some_statement belv:hasEvidence _:1 .
    _:1 belv:hasStatement bel:some_statement ;
        belv:hasCitation <http://bio2rdf.org/pubmed:12345678> ;
        belv:hasEvidenceText """Some quoted evidence text.""" .

In nanopubs using PROV:

    sub:Prov {
        node:1 prov:value "Some quoted evidence text." ;
            prov:wasQuotedFrom <http://www.ncbi.nlm.nih.gov/pubmed/12345678> .
        sub:Ass prov:hadPrimarySource <http://www.ncbi.nlm.nih.gov/pubmed/12345678> ;
            prov:wasDerivedFrom node:1 .
    }


### Third-Party Identifiers

The mapping to third-party identifiers is defined in
[this JSON file](src/main/resources/idschemes.json).


License
-------

BEL2nanopub is free software under the MIT License. See LICENSE.txt.
