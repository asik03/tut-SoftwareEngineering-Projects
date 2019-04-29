#!/bin/sh
if [ $# -eq 1 ]; then
    rm -r /mining_repo 2>nul
    echo "Cloning git repo $1 ..."
    git clone "$1" /mining_repo/
    echo "Running refactoring miner for all commits of $1 ..."
    /refactoringminer/build/distributions/RefactoringMiner/bin/RefactoringMiner -a /mining_repo/
elif [ $# -eq 3 ]; then
    rm -r /mining_repo/ 2>nul
    echo "Cloning git repo $1 ..."
    git clone "$1" /mining_repo/
    echo "Running refactoring miner for commits of $1 ... from $2 to $3"
    /refactoringminer/build/distributions/RefactoringMiner/bin/RefactoringMiner -bc  /mining_repo/ "$2" "$3"
else
    echo "Wrong number of parameters."
    exit 1
fi
