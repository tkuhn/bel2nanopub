BEL2nanopub
===========

This code base transforms BEL documents into nanopublications. In contrast to the mapping to RDF
defined by bel.rb (https://github.com/OpenBEL/bel.rb), we are here trying to use as much as
possible standard URIs of existing vocabularies and ontologies.


Execution
---------

Maven has to be installed and the latest snapshot of nanopub-java:

    $ git clone git@github.com:Nanopublication/nanopub-java.git
    $ cd nanopub-java
    $ mvn install

Before the transformation can be run, some resource files have to be downloaded and mapping tables
have to be built:

    $ scripts/download-files.sh
    $ scripts/CreateIdTables.sh

Then BEL documents can be transformed as follows:

    $ scripts/Bel2Nanopub.sh doc1.bel doc2.bel > output.nanopubs.trig


Examples
--------

See the [example directory](src/main/resources/examples).


Mapping
-------

Below the mapping to RDF and the differences to the mapping by bel.rb are described. 


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

    sub:_3 a rdf:Statement ;
        rdf:subject sub:_1 ;
        rdf:predicate belv:directlyIncreases ;
        rdf:object sub:_2 ;
        occursIn: mesh:D002467 .


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

    sub:provenance {
        sub:_1 prov:value "Some quoted evidence text." ;
            prov:wasQuotedFrom pubmed:12345678 .
        sub:assertion prov:hadPrimarySource pubmed:12345678 ;
            prov:wasDerivedFrom sub:_1 .
    }


### Third-Party Vocabularies

The mapping to identifiers of third-party vocabularies is defined in
[this JSON file](src/main/resources/idschemes.json). The tables for the manual mappings are in the
[tables directory](tables).


### Identifiers with BEL Namespace

The general relations defined by BEL like 'directlyIncreases' are currently not mapped to other
existing vocabularies or ontologies, but we are using URIs in the BEL namespace (the same ones used
by bel.rb, except for some cases where we had to make up new ones).


License
-------

BEL2nanopub is free software under the MIT License. See LICENSE.txt.
