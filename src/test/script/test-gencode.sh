#!/bin/sh

# Auteur : gl42
# Version initiale : 01/01/2024

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"
DIR=src/test/deca/codegen

RED='\033[1;31m'
GREEN='\033[0;32m'
NC='\033[0m'
PASSED="[${GREEN}PASSED${NC}]"
FAILED="[${RED}FAILED${NC}]"

for f in $(find $DIR/valid -type f -name "*.deca"); do
    dst="${f%.deca}.ass"
    rm -f "$dst" 2>/dev/null

    if decac "$f" >/dev/null 2>&1; then
        echo "$PASSED Compilation reussie: decac $f"
    else
        echo "$FAILED Echec de la compilation: decac $f"
        exit 1
    fi

    if [ ! -f "$dst" ]; then
        echo "$FAILED Fichier .ass non genere: decac $f"
        exit 1
    fi

    if ima "$dst" >/dev/null 2>&1; then
        echo "$PASSED Execution reussie: ima $dst"
        rm -f "$dst"
    else
        echo "$FAILED Echec de l'execution: ima $dst"
        rm -f "$dst"
        exit 1
    fi
done

for f in $DIR/invalid/*.deca; do
    dst="${f%.deca}.ass"
    rm -f "$dst" 2>/dev/null

    if decac "$f" >/dev/null 2>&1; then
        echo "$PASSED Compilation reussie: decac $f"
    else
        echo "$FAILED Echec de la compilation: decac $f"
        exit 1
    fi

    if [ ! -f "$dst" ]; then
        echo "$FAILED Fichier .ass non genere: decac $f"
        exit 1
    fi

    if ima "$dst" 2>&1 | grep -q -e "Error"; then
        echo "$PASSED Erreur bien detectee: ima $dst"
        rm -f "$dst"
    else
        echo "$FAILED Erreur non detectee: ima $dst"
        rm -f "$dst"
        exit 1
    fi
done
