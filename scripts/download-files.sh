#!/bin/bash

mkdir -p downloads
cd downloads


# Download BEL namespace mapping:

wget -O nsmap.ttl http://build.openbel.org/artifact/OR-BR/JOB1/build-latest/Namespace-turtle-export/testfile.ttl


# Download BEL annotation resources:

wget -r --no-parent --reject "index.html*" http://resource.belframework.org/belframework/1.0/annotation/
wget -r --no-parent --reject "index.html*" http://resource.belframework.org/belframework/20131211/annotation/


# Download MESH tree-to-ID mapping from Bio2RDF SPARQL endpoint:

# prefix m: <http://bio2rdf.org/mesh_vocabulary:> SELECT ?i ?t WHERE {?x m:mesh-tree-number ?t; m:unique-identifier ?i} LIMIT 10000 OFFSET 0
wget -O meshtreemap-0.csv 'http://mesh.bio2rdf.org/sparql?query=prefix+m%3A+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3A%3E+SELECT+%3Fi+%3Ft+WHERE+{%3Fx+m%3Amesh-tree-number+%3Ft%3B+m%3Aunique-identifier+%3Fi}+LIMIT+10000+OFFSET+0&format=text%2Fcsv&timeout=0'
# prefix m: <http://bio2rdf.org/mesh_vocabulary:> SELECT ?i ?t WHERE {?x m:mesh-tree-number ?t; m:unique-identifier ?i} LIMIT 10000 OFFSET 10000
wget -O meshtreemap-1.csv 'http://mesh.bio2rdf.org/sparql?query=prefix+m%3A+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3A%3E+SELECT+%3Fi+%3Ft+WHERE+{%3Fx+m%3Amesh-tree-number+%3Ft%3B+m%3Aunique-identifier+%3Fi}+LIMIT+10000+OFFSET+10000&format=text%2Fcsv&timeout=0'
# prefix m: <http://bio2rdf.org/mesh_vocabulary:> SELECT ?i ?t WHERE {?x m:mesh-tree-number ?t; m:unique-identifier ?i} LIMIT 10000 OFFSET 20000
wget -O meshtreemap-2.csv 'http://mesh.bio2rdf.org/sparql?query=prefix+m%3A+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3A%3E+SELECT+%3Fi+%3Ft+WHERE+{%3Fx+m%3Amesh-tree-number+%3Ft%3B+m%3Aunique-identifier+%3Fi}+LIMIT+10000+OFFSET+20000&format=text%2Fcsv&timeout=0'
# prefix m: <http://bio2rdf.org/mesh_vocabulary:> SELECT ?i ?t WHERE {?x m:mesh-tree-number ?t; m:unique-identifier ?i} LIMIT 10000 OFFSET 30000
wget -O meshtreemap-3.csv 'http://mesh.bio2rdf.org/sparql?query=prefix+m%3A+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3A%3E+SELECT+%3Fi+%3Ft+WHERE+{%3Fx+m%3Amesh-tree-number+%3Ft%3B+m%3Aunique-identifier+%3Fi}+LIMIT+10000+OFFSET+30000&format=text%2Fcsv&timeout=0'
# prefix m: <http://bio2rdf.org/mesh_vocabulary:> SELECT ?i ?t WHERE {?x m:mesh-tree-number ?t; m:unique-identifier ?i} LIMIT 10000 OFFSET 40000
wget -O meshtreemap-4.csv 'http://mesh.bio2rdf.org/sparql?query=prefix+m%3A+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3A%3E+SELECT+%3Fi+%3Ft+WHERE+{%3Fx+m%3Amesh-tree-number+%3Ft%3B+m%3Aunique-identifier+%3Fi}+LIMIT+10000+OFFSET+40000&format=text%2Fcsv&timeout=0'
# prefix m: <http://bio2rdf.org/mesh_vocabulary:> SELECT ?i ?t WHERE {?x m:mesh-tree-number ?t; m:unique-identifier ?i} LIMIT 10000 OFFSET 50000
wget -O meshtreemap-5.csv 'http://mesh.bio2rdf.org/sparql?query=prefix+m%3A+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3A%3E+SELECT+%3Fi+%3Ft+WHERE+{%3Fx+m%3Amesh-tree-number+%3Ft%3B+m%3Aunique-identifier+%3Fi}+LIMIT+10000+OFFSET+50000&format=text%2Fcsv&timeout=0'

(
  cat meshtreemap-0.csv | sed '1d' ;
  cat meshtreemap-1.csv | sed '1d' ;
  cat meshtreemap-2.csv | sed '1d' ;
  cat meshtreemap-3.csv | sed '1d' ;
  cat meshtreemap-4.csv | sed '1d' ;
  cat meshtreemap-5.csv | sed '1d'
) > meshtreemap.csv

rm meshtreemap-*.csv


# Download mapping from version 1.0 to 20131211:

wget -O change-20131211.json http://resource.belframework.org/belframework/20131211/change_log.json
