#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

echo "Retrieving published nanopubs for Small Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.small_corpus-1.0.nanopubs.trig RA5E1H82MvGpCC-rM690KGQNqQJg8za7VDL8ovoJamZp8

echo "Retrieving published nanopubs for Large Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.large_corpus-1.0.nanopubs.trig RAflqJnKc4t94zr9air2dpSztiy2laEZAyePJQY768Zfo

echo "Retrieving published nanopubs for Small Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.small_corpus-20131211.nanopubs.trig RAXtMFMUPEXI-MCcBE9XzlsdvVBtE7GwZRzsaM0AMym0Y

echo "Retrieving published nanopubs for Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.large_corpus-20131211.nanopubs.trig RAZsyF_g9FGnYoPsa19uiZU-D4tSaFlDfRv52YBbWh7Yc
