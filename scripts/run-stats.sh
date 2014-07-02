#!/bin/bash

scripts/BelStats.sh src/main/resources/examples/*.bel > src/main/resources/stats/3-pubmed-examples.txt
scripts/BelStats.sh downloads/small_corpus-1.0.bel > src/main/resources/stats/small_corpus-1.0.txt
scripts/BelStats.sh downloads/large_corpus-1.0.bel > src/main/resources/stats/large_corpus-1.0.txt
scripts/BelStats.sh downloads/small_corpus-20131211.bel > src/main/resources/stats/small_corpus-20131211.txt
scripts/BelStats.sh downloads/large_corpus-20131211.bel > src/main/resources/stats/large_corpus-20131211.txt
