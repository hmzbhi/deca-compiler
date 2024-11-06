#!/bin/sh

# Auteur : gl42
# Version initiale : 01/01/2024

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/main/bin:"$PATH"
DIR=src/test/deca

RED='\033[1;31m'
GREEN='\033[0;32m'
NC='\033[0m'
PASSED="[${GREEN}PASSED${NC}]"
FAILED="[${RED}FAILED${NC}]"

src=$DIR/context/valid/provided/hello-world.deca
dst="${src%.deca}.ass"

error="L'argument X de l'option -r est manquant"

if decac -r 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

error="L'argument X de l'option -r doit être un entier"

if decac -r X 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

error="L'argument X de l'option -r doit être compris entre 4 et 16"

if decac -r 0 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

error="Les options -p et -v sont incompatibles"

if decac -p -v 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

error="L'option -b doit être utilisée sans fichier source"

if decac -b "$src" 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

error="L'option -b doit être utilisée sans autre option"

if decac -b -d 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

error="<fichier deca> est manquant"

if decac -p -P 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

error="<fichier deca> doit avoir l'extension .deca"

if decac "$dst" 2>&1 | grep -q -e "$error"; then
    echo "$PASSED Erreur bien detectee: $error"
else
    echo "$FAILED Erreur non detectee: $error"
    exit 1
fi

decac "$src" >/dev/null 2>&1

if [ "$?" -ne 0 ]; then
    echo "$FAILED Echec de la compilation: decac $src"
    exit 1
else
    echo "$PASSED Compilation reussie: decac $src"
fi

if [ ! -f "$dst" ]; then
    echo "$FAILED Fichier .ass non genere: decac $src"
    exit 1
else
    echo "$PASSED Fichier .ass bien genere: decac $src"
    rm -f "$dst"
fi

decac_b=$(decac -b)

if [ "$?" -ne 0 ]; then
    echo "$FAILED decac -b a termine avec un status different de zero"
    exit 1
fi

if [ "$decac_b" = "" ]; then
    echo "$FAILED decac -b n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_b" | grep -q -e "Error"; then
    echo "$FAILED La sortie de decac -b contient Error"
    exit 1
fi

if echo "$decac_b" | grep -q -e "ProjetGL2024, GR9, GL42"; then
    echo "$PASSED Pas de probleme detecte avec decac -b"
else
    echo "$FAILED La sortie de decac -b ne contient pas de banniere"
    exit 1
fi

decac=$(decac)

if [ "$?" -ne 0 ]; then
    echo "$FAILED decac a termine avec un status different de zero"
    exit 1
fi

if [ "$decac" = "" ]; then
    echo "$FAILED decac n'a produit aucune sortie"
    exit 1
fi

if echo "$decac" | grep -q -e "Error"; then
    echo "$FAILED La sortie de decac contient Error"
    exit 1
fi

if echo "$decac" | grep -q -e "OPTIONS"; then
    echo "$PASSED Pas de probleme detecte avec decac"
else
    echo "$FAILED La sortie de decac n'affiche pas les options"
    exit 1
fi

for f in $DIR/syntax/valid/*/*.deca; do
    decac_p=$(decac -p "$f")

    if [ "$?" -ne 0 ]; then
        echo "$FAILED decac -p a termine avec un status different de zero: decac -p $f"
        exit 1
    fi

    if echo "$decac_p" | grep -q -e "Error"; then
        echo "$FAILED La sortie de decac -p contient Error: decac -p $f"
        exit 1
    fi

    dst="${f%.deca}2.deca"
    echo "$decac_p" >"$dst"
    res=$(decac -p "$dst")

    if [ "$?" -ne 0 ]; then
        echo "$FAILED La sortie de decac -p est un programme syntaxiquement incorrect: decac -p $f"
        rm -f "$dst" 2>/dev/null
        exit 1
    fi

    if [ ! "$decac_p" = "$res" ]; then
        echo "$FAILED La decompilation avec decac -p est non idempotente: decac -p $f"
        rm -f "$dst" 2>/dev/null
        exit 1
    fi
    
    rm -f "$dst" 2>/dev/null
done

echo "$PASSED Pas de probleme detecte avec decac -p"

for f in $(find $DIR/context/valid -type f -name "*.deca"); do
    decac_v=$(decac -v "$f")

    if [ ! "$decac_v" = "" ]; then
        echo "$FAILED decac -v a produit une sortie: decac -v $f"
        exit 1
    fi
done

for f in $(find $DIR/context/invalid -type f -name "*.deca"); do
    if ! decac -v "$f" 2>&1 | grep -q -e "$f:[0-9][0-9]*:[0-9][0-9]*:"; then
        echo "$FAILED decac -v n'a pas produit de message d'erreur: decac -v $f"
        exit 1
    fi
done

echo "$PASSED Pas de probleme detecte avec decac -v"

for f in $DIR/codegen/invalid/*.deca; do
    dst="${f%.deca}.ass"
    rm -f "$dst" 2>/dev/null
    decac -n "$f" >/dev/null 2>&1

    if ima "$dst" 2>&1 | grep -q -e "Error"; then
        echo "$FAILED Tests a l'execution non supprimes: ima $dst"
        rm -f "$dst"
        exit 1
    fi
done

echo "$PASSED Pas de probleme detecte avec decac -n"

src=$DIR/decac/test_decacr.deca
decac_r=$(decac -r 4 "$src")

if [ "$?" -ne 0 ]; then
    echo "$FAILED decac -r a termine avec un status different de zero: decac -r 4 $src"
    exit 1
fi

if echo "$decac_r" | grep -q -e "R5"; then
    echo "$FAILED Un registre indisponible est utilise: decac -r 4 $src"
    exit 1
else
    echo "$PASSED Pas de probleme detecte avec decac -r"
fi

src=$DIR/context/valid/provided/hello-world.deca
decac_d=$(decac -d "$src")

if [ "$?" -ne 0 ]; then
    echo "$FAILED decac -d a termine avec un status different de zero: decac -d $src"
    exit 1
fi

if ! echo "$decac_d" | grep -q -e "INFO"; then
    echo "$FAILED La sortie de decac -d n'affiche pas les traces de debug: decac -d $src"
    exit 1
fi

decac_d=$(decac -d -d "$src")

if ! echo "$decac_d" | grep -q -e "DEBUG"; then
    echo "$FAILED La sortie de decac -d n'affiche pas les traces de debug: decac -d -d $src"
    exit 1
fi

echo "$PASSED Pas de probleme detecte avec decac -d"

src=$DIR/codegen/valid/provided/cond0.deca
src2=$DIR/codegen/valid/provided/ecrit0.deca
dst="${src%.deca}.ass"
dst2="${src2%.deca}.ass"

decac "$src" "$src2"
res=$(cat "$dst" "$dst2")
decac -P "$src" "$src2"

if [ "$?" -ne 0 ]; then
    echo "$FAILED decac -P a termine avec un status different de zero: decac -P $src $src2"
    rm -f "$dst" "$dst2"
    exit 1
fi

if [ ! "$res" = "$(cat "$dst" "$dst2")" ]; then
    echo "$FAILED decac -P a produit une erreur: decac -P $src $src2"
    rm -f "$dst" "$dst2"
    exit 1
fi

rm -f "$dst" "$dst2"
echo "$PASSED Pas de probleme detecte avec decac -P"