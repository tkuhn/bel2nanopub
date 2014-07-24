#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

echo "Making index for Small Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.index.MakeIndex  \
  -o output/index.small_corpus-1.0.nanopubs.trig \
  -t "Nanopubs converted from OpenBEL's Small Corpus 1.0" \
  -d "These nanopubs were automatically converted from OpenBEL's Small Corpus version 1.0." \
  -a http://resource.belframework.org/belframework/1.0/knowledge/small_corpus.bel \
  -a https://github.com/tkuhn/bel2nanopub \
  -c 0000-0002-1267-0234 -c 0000-0001-6818-334X \
  output/small_corpus-1.0.nanopubs.trig

echo "Making index for Large Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.index.MakeIndex  \
  -o output/index.large_corpus-1.0.nanopubs.trig \
  -t "Nanopubs converted from OpenBEL's Large Corpus 1.0" \
  -d "These nanopubs were automatically converted from OpenBEL's Large Corpus version 1.0." \
  -a http://resource.belframework.org/belframework/1.0/knowledge/large_corpus.bel \
  -a https://github.com/tkuhn/bel2nanopub \
  -c 0000-0002-1267-0234 -c 0000-0001-6818-334X \
  output/large_corpus-1.0.nanopubs.trig

echo "Making index for Small Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.index.MakeIndex  \
  -o output/index.small_corpus-20131211.nanopubs.trig \
  -t "Nanopubs converted from OpenBEL's Small Corpus 20131211" \
  -d "These nanopubs were automatically converted from OpenBEL's Small Corpus version 20131211." \
  -a http://resource.belframework.org/belframework/20131211/knowledge/small_corpus.bel \
  -a https://github.com/tkuhn/bel2nanopub \
  -c 0000-0002-1267-0234 -c 0000-0001-6818-334X \
  output/small_corpus-20131211.nanopubs.trig

echo "Making index for Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.index.MakeIndex  \
  -o output/index.large_corpus-20131211.nanopubs.trig \
  -t "Nanopubs converted from OpenBEL's Large Corpus 20131211" \
  -d "These nanopubs were automatically converted from OpenBEL's Large Corpus version 20131211." \
  -a http://resource.belframework.org/belframework/20131211/knowledge/large_corpus.bel \
  -a https://github.com/tkuhn/bel2nanopub \
  -c 0000-0002-1267-0234 -c 0000-0001-6818-334X \
  output/large_corpus-20131211.nanopubs.trig
