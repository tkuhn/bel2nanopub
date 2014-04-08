#!/bin/bash
#
# The following environment variable can be used:
# - JAVA_OPTS: Can be used to set Java command line options

CLASS=ch.tkuhn.bel2nanopub.Bel2Nanopub

DIR=`pwd`
cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..
BEL2NANOPUBJAVADIR=`pwd`

# for Cygwin:
BEL2NANOPUBJAVADIR=${BEL2NANOPUBJAVADIR#/cygdrive/?}

cd $DIR

mvn -q -e -f $BEL2NANOPUBJAVADIR/pom.xml exec:java -Dexec.mainClass="$CLASS" -Dexec.args="$*"
