#!/bin/bash
#
# The following environment variable can be used:
# - JAVA_OPTS: Can be used to set Java command line options

CLASS=ch.tkuhn.bel2nanopub.CreateIdTables

WORKINGDIR=`pwd`
cd "$( dirname "${BASH_SOURCE[0]}" )"
cd ..
PROJECTDIR=`pwd`

# for Cygwin:
PROJECTDIR=${PROJECTDIR#/cygdrive/?}

if [ ! -f classpath.txt ]; then
  echo "classpath.txt not found: Run 'mvn clean package' first."
  exit 1
fi

CP=$PROJECTDIR/target/classes:$(cat classpath.txt)

cd $WORKINGDIR

java -cp $CP $JAVA_OPTS $CLASS "$@"
