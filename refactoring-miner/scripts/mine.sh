#!/bin/sh
export CLASSPATH=lib/RefactoringMiner.jar:lib/org.eclipse.jgit-5.2.1.201812262042-r.jar:lib/slf4j-log4j12-1.7.7.jar:lib/junit-4.11.jar:lib/org.eclipse.jdt.core-3.16.0.jar:lib/commons-lang3-3.8.1.jar:lib/github-api-1.95.jar:lib/jsch-0.1.54.jar:lib/jzlib-1.1.1.jar:lib/JavaEWAH-1.1.6.jar:lib/log4j-1.2.17.jar:lib/hamcrest-core-1.3.jar:lib/org.eclipse.core.resources-3.13.200.jar:lib/org.eclipse.core.runtime-3.15.100.jar:lib/org.eclipse.core.filesystem-1.7.200.jar:lib/org.eclipse.text-3.8.0.jar:lib/commons-codec-1.7.jar:lib/jackson-databind-2.9.2.jar:lib/commons-io-1.4.jar:lib/org.eclipse.core.expressions-3.6.200.jar:lib/org.eclipse.osgi-3.13.200.jar:lib/org.eclipse.equinox.common-3.10.200.jar:lib/org.eclipse.core.jobs-3.10.200.jar:lib/org.eclipse.equinox.registry-3.8.200.jar:lib/org.eclipse.equinox.preferences-3.7.200.jar:lib/org.eclipse.core.contenttype-3.7.200.jar:lib/org.eclipse.equinox.app-1.4.0.jar:lib/org.eclipse.core.commands-3.9.200.jar:lib/jackson-annotations-2.9.0.jar:lib/jackson-core-2.9.2.jar:lib/slf4j-api-1.7.7.jar

if (( $# == 1 )); then
    rmdir -p mining_repo/ 2>nul
    echo "Cloning gir repo $1 ..."
    git clone "$1" mining_repo/
    echo "Running refactoring miner for all commits of $1 ..."
    java -classpath "$CLASSPATH" org.refactoringminer.RefactoringMiner -a mining_repo/
elif (( $# == 3 )); then
    rmdir -p mining_repo/ 2>nul
    echo "Cloning gir repo $1 ..."
    git clone "$1" mining_repo/
    echo "Running refactoring miner for commits of $1 ... from $2 to $3"
    java -classpath "$CLASSPATH" org.refactoringminer.RefactoringMiner -bc  mining_repo/ "$2" "$3"
else
    echo "Wrong number of parameters."
    exit 1
fi