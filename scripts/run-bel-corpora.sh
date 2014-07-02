#!/bin/bash

# ORCID IDs of creators:
ORCIDS="-c 0000-0002-1267-0234 -c 0000-0001-6818-334X"

URL1=http://resource.belframework.org/belframework/1.0/knowledge
URL2=http://resource.belframework.org/belframework/20131211/knowledge

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

mkdir -p downloads
mkdir -p output

echo "Downloading files for version 1.0..."
wget -O downloads/small_corpus-1.0-original.bel $URL1/small_corpus.bel
wget -O downloads/large_corpus-1.0-original.bel $URL1/large_corpus.bel

echo "Downloading files for version 20131211..."
wget -O downloads/small_corpus-20131211-original.bel $URL2/small_corpus.bel
wget -O downloads/large_corpus-20131211-original.bel $URL2/large_corpus.bel

echo "Preprocessing files (remove carriage return characters)..."
cat downloads/small_corpus-1.0-original.bel | sed 's/\r//' > downloads/small_corpus-1.0.bel
cat downloads/large_corpus-1.0-original.bel | sed 's/\r//' > downloads/large_corpus-1.0.bel
cat downloads/small_corpus-20131211-original.bel | sed 's/\r//' > downloads/small_corpus-20131211.bel
cat downloads/large_corpus-20131211-original.bel | sed 's/\r//' > downloads/large_corpus-20131211.bel

echo "Processing small corpus of version 1.0..."
scripts/Bel2Nanopub.sh $ORCIDS -u $URL1/small_corpus.bel downloads/small_corpus-1.0.bel > output/small_corpus-1.0.nanopubs.trig 2> output/small_corpus-1.0.nanopubs.err

echo "Processing large corpus of version 1.0..."
scripts/Bel2Nanopub.sh $ORCIDS -u $URL1/large_corpus.bel downloads/large_corpus-1.0.bel > output/large_corpus-1.0.nanopubs.trig 2> output/large_corpus-1.0.nanopubs.err

echo "Processing small corpus of version 20131211..."
scripts/Bel2Nanopub.sh $ORCIDS -u $URL2/small_corpus.bel downloads/small_corpus-20131211.bel > output/small_corpus-20131211.nanopubs.trig 2> output/small_corpus-20131211.nanopubs.err

echo "Processing large corpus of version 20131211..."
scripts/Bel2Nanopub.sh $ORCIDS -u $URL2/large_corpus.bel downloads/large_corpus-20131211.bel > output/large_corpus-20131211.nanopubs.trig 2> output/large_corpus-20131211.nanopubs.err
