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
  -s http://np.inn.ac/RAR7OfS-AqG9_XogObQZpWq6LaBsV95jeseJtscuwpwJo \
  -s http://np.inn.ac/RAsmhDKpyhTORhvWOZZL_cfSVZUqLYJFl_AqF_xPN34Qw

echo "Making index for Small/Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.index.MakeIndex  \
  -o output/index.corpora-20131211.nanopubs.trig \
  -t "Nanopubs converted from OpenBEL's Small and Large Corpus 20131211" \
  -d "These nanopubs were automatically converted from OpenBEL's Small/Large Corpus version 20131211." \
  -a http://resource.belframework.org/belframework/1.0/knowledge/small_corpus.bel \
  -a https://github.com/tkuhn/bel2nanopub \
  -c 0000-0002-1267-0234 -c 0000-0001-6818-334X \
  -s http://np.inn.ac/RAtF0ivB9B8cb-u3K_zElgmRBxiDwfym1yVBRY6VAyWvE \
  -s http://np.inn.ac/RAdw0S5f06-Ed2uXNISU-wbXmqvQ9-6hxd4fqIslT38Wg
