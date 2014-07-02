#!/bin/bash

mkdir -p downloads
cd downloads


# Download BEL namespace mapping:

wget -O nsmap.ttl http://build.openbel.org/browse/OR-BR/latestSuccessful/artifact/shared/Namespace-turtle-export/testfile.ttl


# Download BEL annotation resources:

wget -r --no-parent --reject "index.html*" http://resource.belframework.org/belframework/1.0/annotation/
wget -r --no-parent --reject "index.html*" http://resource.belframework.org/belframework/20131211/annotation/


# Download MESH tree-to-ID mapping from Bio2RDF SPARQL endpoint:

# SELECT ?i ?t WHERE {?x <http://bio2rdf.org/mesh_vocabulary:mesh-tree-number> ?t; <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?i} LIMIT 10000 OFFSET 0
wget -O meshtreemap-0.csv 'http://mesh.bio2rdf.org/sparql?query=SELECT+%3Fi+%3Ft+WHERE+%7B%3Fx+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3Amesh-tree-number%3E+%3Ft%3B+%3Chttp%3A%2F%2Fbio2rdf.org%2Fbio2rdf_vocabulary%3Aidentifier%3E+%3Fi%7D+LIMIT+10000+OFFSET+0&format=text%2Fcsv&timeout=0'
# SELECT ?i ?t WHERE {?x <http://bio2rdf.org/mesh_vocabulary:mesh-tree-number> ?t; <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?i} LIMIT 10000 OFFSET 10000
wget -O meshtreemap-1.csv 'http://mesh.bio2rdf.org/sparql?query=SELECT+%3Fi+%3Ft+WHERE+%7B%3Fx+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3Amesh-tree-number%3E+%3Ft%3B+%3Chttp%3A%2F%2Fbio2rdf.org%2Fbio2rdf_vocabulary%3Aidentifier%3E+%3Fi%7D+LIMIT+10000+OFFSET+10000&format=text%2Fcsv&timeout=0'
# SELECT ?i ?t WHERE {?x <http://bio2rdf.org/mesh_vocabulary:mesh-tree-number> ?t; <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?i} LIMIT 10000 OFFSET 20000
wget -O meshtreemap-2.csv 'http://mesh.bio2rdf.org/sparql?query=SELECT+%3Fi+%3Ft+WHERE+%7B%3Fx+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3Amesh-tree-number%3E+%3Ft%3B+%3Chttp%3A%2F%2Fbio2rdf.org%2Fbio2rdf_vocabulary%3Aidentifier%3E+%3Fi%7D+LIMIT+10000+OFFSET+20000&format=text%2Fcsv&timeout=0'
# SELECT ?i ?t WHERE {?x <http://bio2rdf.org/mesh_vocabulary:mesh-tree-number> ?t; <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?i} LIMIT 10000 OFFSET 30000
wget -O meshtreemap-3.csv 'http://mesh.bio2rdf.org/sparql?query=SELECT+%3Fi+%3Ft+WHERE+%7B%3Fx+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3Amesh-tree-number%3E+%3Ft%3B+%3Chttp%3A%2F%2Fbio2rdf.org%2Fbio2rdf_vocabulary%3Aidentifier%3E+%3Fi%7D+LIMIT+10000+OFFSET+30000&format=text%2Fcsv&timeout=0'
# SELECT ?i ?t WHERE {?x <http://bio2rdf.org/mesh_vocabulary:mesh-tree-number> ?t; <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?i} LIMIT 10000 OFFSET 40000
wget -O meshtreemap-4.csv 'http://mesh.bio2rdf.org/sparql?query=SELECT+%3Fi+%3Ft+WHERE+%7B%3Fx+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3Amesh-tree-number%3E+%3Ft%3B+%3Chttp%3A%2F%2Fbio2rdf.org%2Fbio2rdf_vocabulary%3Aidentifier%3E+%3Fi%7D+LIMIT+10000+OFFSET+40000&format=text%2Fcsv&timeout=0'
# SELECT ?i ?t WHERE {?x <http://bio2rdf.org/mesh_vocabulary:mesh-tree-number> ?t; <http://bio2rdf.org/bio2rdf_vocabulary:identifier> ?i} LIMIT 10000 OFFSET 50000
wget -O meshtreemap-5.csv 'http://mesh.bio2rdf.org/sparql?query=SELECT+%3Fi+%3Ft+WHERE+%7B%3Fx+%3Chttp%3A%2F%2Fbio2rdf.org%2Fmesh_vocabulary%3Amesh-tree-number%3E+%3Ft%3B+%3Chttp%3A%2F%2Fbio2rdf.org%2Fbio2rdf_vocabulary%3Aidentifier%3E+%3Fi%7D+LIMIT+10000+OFFSET+50000&format=text%2Fcsv&timeout=0'

(
  cat meshtreemap-0.csv | sed '1d' ;
  cat meshtreemap-1.csv | sed '1d' ;
  cat meshtreemap-2.csv | sed '1d' ;
  cat meshtreemap-3.csv | sed '1d' ;
  cat meshtreemap-4.csv | sed '1d' ;
  cat meshtreemap-5.csv | sed '1d'
) | sed sed 's/"\(.*\)","http:\/\/bio2rdf\.org\/mesh:\(.*\)"/\1,\2/' > meshtreemap.csv

rm meshtreemap-*.csv


# Download mapping from version 1.0 to 20131211:

wget -O change-20131211.json http://resource.belframework.org/belframework/20131211/change_log.json


# Download small/large corpus for version 1.0

URL=http://resource.belframework.org/belframework/1.0/knowledge

wget -O small_corpus-1.0-original.bel $URL/small_corpus.bel
wget -O large_corpus-1.0-original.bel $URL/large_corpus.bel


# Download small/large corpus for version 20131211

URL=http://resource.belframework.org/belframework/20131211/knowledge

wget -O small_corpus-20131211-original.bel $URL/small_corpus.bel
wget -O large_corpus-20131211-original.bel $URL/large_corpus.bel


# Remove carriage return characters from BEL documents

cat small_corpus-1.0-original.bel | sed 's/\r//' > small_corpus-1.0.bel
cat large_corpus-1.0-original.bel | sed 's/\r//' > large_corpus-1.0.bel
cat small_corpus-20131211-original.bel | sed 's/\r//' > small_corpus-20131211.bel
cat large_corpus-20131211-original.bel | sed 's/\r//' > large_corpus-20131211.bel
