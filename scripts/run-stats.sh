#!/bin/bash

OPENBEL_KNOWLEDGE_DIR=~/Packages/openbel-framework-resources/knowledge

scripts/BelStats.sh src/main/resources/examples/*.bel > src/main/resources/stats/3-pubmed-examples.txt
scripts/BelStats.sh $OPENBEL_KNOWLEDGE_DIR/small_corpus.bel > src/main/resources/stats/small_corpus.txt
scripts/BelStats.sh $OPENBEL_KNOWLEDGE_DIR/large_corpus.bel > src/main/resources/stats/large_corpus.txt
