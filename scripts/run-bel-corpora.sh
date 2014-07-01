#!/bin/bash

# ORCID IDs of creators:
ORCIDS="-c 0000-0002-1267-0234 -c 0000-0001-6818-334X"

NS=http://resource.belframework.org/belframework/1.0/knowledge

BELDIR=~/Packages/openbel-framework-resources

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

mkdir -p temp
mkdir -p output

#echo "Downloading files..."
#wget -O temp/small_corpus.original.bel http://resource.belframework.org/belframework/20131211/knowledge/small_corpus.bel
#wget -O temp/large_corpus.original.bel http://resource.belframework.org/belframework/20131211/knowledge/large_corpus.bel
cp $BELDIR/knowledge/small_corpus.bel temp/small_corpus.original.bel
cp $BELDIR/knowledge/large_corpus.bel temp/large_corpus.original.bel

echo "Preprocessing files (remove carriage return characters)..."
cat temp/small_corpus.original.bel | sed 's/\r//' > temp/small_corpus.bel
cat temp/large_corpus.original.bel | sed 's/\r//' > temp/large_corpus.bel

echo "Processing small corpus..."
scripts/Bel2Nanopub.sh $ORCIDS -u https://github.com/OpenBEL/openbel-framework-resources/blob/latest/knowledge/small_corpus.bel temp/small_corpus.bel > output/small_corpus.nanopubs.trig 2> output/small_corpus.nanopubs.err

echo "Processing large corpus..."
scripts/Bel2Nanopub.sh $ORCIDS -u https://github.com/OpenBEL/openbel-framework-resources/blob/latest/knowledge/large_corpus.bel temp/large_corpus.bel > output/large_corpus.nanopubs.trig 2> output/large_corpus.nanopubs.err
