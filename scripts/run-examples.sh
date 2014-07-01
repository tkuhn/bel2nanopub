#!/bin/bash

# ORCID IDs of creators:
ORCIDS="-c 0000-0002-1267-0234 -c 0000-0001-6818-334X"

# Timestamp (only for regression testing):
TIMESTAMP="2014-07-01T15:39:00.028+02:00"

NS=http://resource.belframework.org/belframework/1.0/knowledge

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

scripts/Bel2Nanopub.sh $ORCIDS -t $TIMESTAMP -u $NS/full_abstract1.bel src/main/resources/examples/pubmed9202001.bel > src/main/resources/examples/pubmed9202001.nanopubs.trig
scripts/Bel2Nanopub.sh $ORCIDS -t $TIMESTAMP -u $NS/full_abstract2.bel src/main/resources/examples/pubmed14734561.bel > src/main/resources/examples/pubmed14734561.nanopubs.trig
scripts/Bel2Nanopub.sh $ORCIDS -t $TIMESTAMP -u $NS/full_abstract3.bel src/main/resources/examples/pubmed16679305.bel > src/main/resources/examples/pubmed16679305.nanopubs.trig

# Comment out to also create the bel.rb files:
exit

H='# File produced by bel.rb (https://github.com/OpenBEL/bel.rb)'

( echo $H ; bel2rdf --bel src/main/resources/examples/pubmed9202001.bel -f turtle ) > src/main/resources/examples/pubmed9202001.ttl
( echo $H ; bel2rdf --bel src/main/resources/examples/pubmed14734561.bel -f turtle ) > src/main/resources/examples/pubmed14734561.ttl
( echo $H ; bel2rdf --bel src/main/resources/examples/pubmed16679305.bel -f turtle ) > src/main/resources/examples/pubmed16679305.ttl
