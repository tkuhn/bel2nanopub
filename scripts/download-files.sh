#!/bin/bash

mkdir -p downloads

wget -O downloads/nsmap.ttl http://build.openbel.org/browse/OR-BR/latest/artifact/JOB1/Namespace-turtle-export/testfile.ttl
# if latest build failed:
#wget -O downloads/nsmap.ttl http://build.openbel.org/browse/OR-BR-84/artifact/JOB1/Namespace-turtle-export/testfile.ttl

wget -O downloads/hgnc-human-genes.belns http://resource.belframework.org/belframework/20131211/namespace/hgnc-human-genes.belns
