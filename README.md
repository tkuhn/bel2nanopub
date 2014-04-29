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

The mapping to third-party identifiers is defined in
[this JSON file](src/main/resources/idschemes.json).

Still to be mapped:

- http://resource.belframework.org/belframework/1.0/namespace/affy-*.belns
- http://resource.belframework.org/belframework/20131211/namespace/affy-probeset-ids.belns
- http://resource.belframework.org/belframework/20131211/namespace/disease-ontology.belns
- http://resource.belframework.org/belframework/20131211/namespace/disease-ontology-ids.belns
- http://resource.belframework.org/belframework/1.0/annotation/atcc-cell-line.belanno
- http://resource.belframework.org/belframework/20131211/annotation/anatomy.belanno
- http://resource.belframework.org/belframework/20131211/annotation/cell-line.belanno
- http://resource.belframework.org/belframework/20131211/annotation/cell.belanno
- http://resource.belframework.org/belframework/20131211/annotation/disease.belanno


License
-------

BEL2nanopub is free software under the MIT License. See LICENSE.txt.
