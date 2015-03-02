#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

echo "Retrieving published nanopubs for Small Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.small_corpus-1.0.nanopubs.trig RAR7OfS-AqG9_XogObQZpWq6LaBsV95jeseJtscuwpwJo

echo "Retrieving published nanopubs for Large Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.large_corpus-1.0.nanopubs.trig RAsmhDKpyhTORhvWOZZL_cfSVZUqLYJFl_AqF_xPN34Qw

echo "Retrieving published nanopubs for Small/Large Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.corpora-1.0.nanopubs.trig RAKUB-H_7ATOS2wemqN2yXjGdOqqlYeOuAgy5WOgVQwpg

echo "Retrieving published nanopubs for Small Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.small_corpus-20131211.nanopubs.trig RAtF0ivB9B8cb-u3K_zElgmRBxiDwfym1yVBRY6VAyWvE

echo "Retrieving published nanopubs for Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.large_corpus-20131211.nanopubs.trig RAdw0S5f06-Ed2uXNISU-wbXmqvQ9-6hxd4fqIslT38Wg

echo "Retrieving published nanopubs for Small/Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.corpora-20131211.nanopubs.trig RAVEEQwjHbSgw0a7WIZhVkVHOnbfTGY7HNTSDGbrkPAAg
