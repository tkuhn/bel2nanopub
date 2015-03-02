#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..

echo "Retrieving published nanopubs for Small Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.small_corpus-1.0.nanopubs.trig RAwca4sgaf81LsmLpFxCDnr8-z7PZxuFaq_JgzHR-M7Zw

echo "Retrieving published nanopubs for Large Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.large_corpus-1.0.nanopubs.trig RAaXIwHHNGrYbQQqQ_4CrTfKozhgxs2Oxzgr8_k8vYxCo

echo "Retrieving published nanopubs for Small/Large Corpus 1.0..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.corpora-1.0.nanopubs.trig RAh69y-NnlTMvakrVrJUrJEW6n0WXUxlwUwEXHai5RocU

echo "Retrieving published nanopubs for Small Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.small_corpus-20131211.nanopubs.trig RAsY_oMj-L_wka_Uf7HvAryVHP6fgG5bnJv9ctxuehKCw

echo "Retrieving published nanopubs for Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.large_corpus-20131211.nanopubs.trig RABWMIlHMyKifOPh3Es_c261oGiENsgYu_vUzBGQEZimY

echo "Retrieving published nanopubs for Small/Large Corpus 20131211..."
scripts/exec-class.sh org.nanopub.extra.server.GetNanopub -c -o output/downloaded.corpora-20131211.nanopubs.trig RAOIcxR5izr0EPkdLRHEyP2HkRYlGU6vkmB0Qcfu0UZEw
