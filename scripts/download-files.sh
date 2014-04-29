#!/bin/bash

mkdir -p downloads
cd downloads

wget -O nsmap.ttl http://build.openbel.org/artifact/OR-BR/JOB1/build-latest/Namespace-turtle-export/testfile.ttl

wget -r --no-parent --reject "index.html*" http://resource.belframework.org/belframework/1.0/annotation/
wget -r --no-parent --reject "index.html*" http://resource.belframework.org/belframework/20131211/annotation/

wget -O meshtreemap.csv 'http://mesh.bio2rdf.org/sparql?query=prefix+m%3A+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3A%3E+SELECT+%3Fi+%3Ft+WHERE+{%3Fx+m%3Amesh-tree-number+%3Ft%3B+m%3Aunique-identifier+%3Fi}&format=text%2Fcsv&timeout=0'
