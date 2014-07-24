#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

echo "Publishing nanopubs for Small/Large Corpus 1.0/20131211..."
scripts/exec-class.sh org.nanopub.extra.server.PublishNanopub \
  output/small_corpus-1.0.nanopubs.trig \
  output/index.small_corpus-1.0.nanopubs.trig \
  output/large_corpus-1.0.nanopubs.trig \
  output/index.large_corpus-1.0.nanopubs.trig \
  output/small_corpus-20131211.nanopubs.trig \
  output/index.small_corpus-20131211.nanopubs.trig \
  output/large_corpus-20131211.nanopubs.trig \
  output/index.large_corpus-20131211.nanopubs.trig
