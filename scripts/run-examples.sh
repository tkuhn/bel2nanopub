#!/bin/bash

# Put your Orcid ID here:
ORCID=0000-0002-1267-0234

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

scripts/Bel2Nanopub.sh -c $ORCID src/main/resources/examples/pubmed9202001.bel > src/main/resources/examples/pubmed9202001.out.txt
scripts/Bel2Nanopub.sh -c $ORCID src/main/resources/examples/pubmed14734561.bel > src/main/resources/examples/pubmed14734561.out.txt
scripts/Bel2Nanopub.sh -c $ORCID src/main/resources/examples/pubmed16679305.bel > src/main/resources/examples/pubmed16679305.out.txt
