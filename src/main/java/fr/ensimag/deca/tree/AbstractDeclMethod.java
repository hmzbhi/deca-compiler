package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

public abstract class AbstractDeclMethod extends Tree {

    protected abstract void verifyDeclMethod(DecacCompiler compiler
            , ClassDefinition currentClass)
            throws ContextualError;

    protected abstract void verifyDeclMethodBody(DecacCompiler compiler
            , ClassDefinition currentClass)
            throws ContextualError;


    public abstract void codeGenMethodTable(DecacCompiler compiler, Symbol className);

    public abstract void codeGenMethod(DecacCompiler compiler, Symbol className);
}
