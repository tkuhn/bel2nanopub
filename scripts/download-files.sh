#!/bin/bash

mkdir -p downloads

wget -O downloads/nsmap.ttl http://build.openbel.org/artifact/OR-BR/JOB1/build-latest/Namespace-turtle-export/testfile.ttl

wget -O downloads/hgnc-human-genes.belns http://build.openbel.org/artifact/OR-BR/JOB1/build-latest/Namespaces/hgnc-human-genes.belns
