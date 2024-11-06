package fr.ensimag.deca.codegen;

import java.util.HashMap;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;

/**
 * VTable
 */
public class VTable extends HashMap<Symbol, Label[]> {

    public VTable(DecacCompiler compiler) {
        Label list[] = new Label[1];
        list[0] = new Label("code.Object.equals");
        put(compiler.createSymbol("Object"), list);
    }
}