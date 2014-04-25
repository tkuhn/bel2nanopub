#!/bin/bash

# Put your Orcid ID here:
ORCID=0000-0002-1267-0234

# Timestamp (only for regression testing):
TIMESTAMP="2014-04-25T12:57:00.028+02:00"

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

scripts/Bel2Nanopub.sh -c $ORCID -t $TIMESTAMP src/main/resources/examples/pubmed9202001.bel > src/main/resources/examples/pubmed9202001.out.txt
scripts/Bel2Nanopub.sh -c $ORCID -t $TIMESTAMP src/main/resources/examples/pubmed14734561.bel > src/main/resources/examples/pubmed14734561.out.txt
scripts/Bel2Nanopub.sh -c $ORCID -t $TIMESTAMP src/main/resources/examples/pubmed16679305.bel > src/main/resources/examples/pubmed16679305.out.txt

H='# File produced by bel.rb (https://github.com/OpenBEL/bel.rb)'

( echo $H ; bel2rdf --bel src/main/resources/examples/pubmed9202001.bel -f turtle ) > src/main/resources/examples/pubmed9202001.ttl
( echo $H ; bel2rdf --bel src/main/resources/examples/pubmed14734561.bel -f turtle ) > src/main/resources/examples/pubmed14734561.ttl
( echo $H ; bel2rdf --bel src/main/resources/examples/pubmed16679305.bel -f turtle ) > src/main/resources/examples/pubmed16679305.ttl
