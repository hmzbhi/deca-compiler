#!/bin/sh

# Auteur : gl42
# Version initiale : 01/01/2024

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
DIR=src/test/deca/context
LAUNCHER=test_context

RED='\033[1;31m'
GREEN='\033[0;32m'
NC='\033[0m'
PASSED="[${GREEN}PASSED${NC}]"
FAILED="[${RED}FAILED${NC}]"

for f in $(find $DIR/valid -type f -name "*.deca"); do
    if $LAUNCHER "$f" 2>&1 | grep -q -e ":[0-9][0-9]*:"; then
        echo "$FAILED Erreur inattendue: $LAUNCHER $f"
        exit 1
    else
        echo "$PASSED Analyse reussie: $LAUNCHER $f"
    fi
done

for f in $(find $DIR/invalid -type f -name "*.deca"); do
    if $LAUNCHER "$f" 2>&1 | grep -q -e "$f:[0-9][0-9]*:[0-9][0-9]*:"; then
        echo "$PASSED Erreur bien detectee: $LAUNCHER $f"
    else
        echo "$FAILED Erreur non detectee: $LAUNCHER $f"
        exit 1
    fi
done
