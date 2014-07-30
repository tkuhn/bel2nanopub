#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

echo "Making index for Small/Large Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.index.MakeIndex  \
  -o output/index.corpora-1.0.nanopubs.trig \
  -t "Nanopubs converted from OpenBEL's Small and Large Corpus 1.0" \
  -d "These nanopubs were automatically converted from OpenBEL's Small/Large Corpus version 1.0." \
  -a http://resource.belframework.org/belframework/1.0/knowledge/small_corpus.bel \
  -a https://github.com/tkuhn/bel2nanopub \
  -c 0000-0002-1267-0234 -c 0000-0001-6818-334X \
  -s http://np.inn.ac/RA5E1H82MvGpCC-rM690KGQNqQJg8za7VDL8ovoJamZp8 \
  -s http://np.inn.ac/RAflqJnKc4t94zr9air2dpSztiy2laEZAyePJQY768Zfo

echo "Making index for Small/Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.index.MakeIndex  \
  -o output/index.corpora-20131211.nanopubs.trig \
  -t "Nanopubs converted from OpenBEL's Small and Large Corpus 20131211" \
  -d "These nanopubs were automatically converted from OpenBEL's Small/Large Corpus version 20131211." \
  -a http://resource.belframework.org/belframework/1.0/knowledge/small_corpus.bel \
  -a https://github.com/tkuhn/bel2nanopub \
  -c 0000-0002-1267-0234 -c 0000-0001-6818-334X \
  -s http://np.inn.ac/RAXtMFMUPEXI-MCcBE9XzlsdvVBtE7GwZRzsaM0AMym0Y \
  -s http://np.inn.ac/RAZsyF_g9FGnYoPsa19uiZU-D4tSaFlDfRv52YBbWh7Yc
