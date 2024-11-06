package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.ObjectClass;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;

import org.apache.log4j.Logger;

/**
 *
 * @author gl42
 * @date 01/01/2024
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");

        for(AbstractDeclClass declclass : this.getList()){
            declclass.verifyClass(compiler);
        }

        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass declclass : this.getList()) {
            declclass.verifyClassMembers(compiler);
        }
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass declclass : this.getList()) {
            declclass.verifyClassBody(compiler);
        }
    }

    public void codeGenListMethodTable(DecacCompiler compiler) {
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("       Construction des tables des methodes");
        compiler.addComment("--------------------------------------------------");

        ObjectClass.codeGenMethodTable(compiler);

        for (AbstractDeclClass i : getList()) {
            i.codeGenMethodTable(compiler);
        }
    }

    public void codeGenListClass(DecacCompiler compiler) {
        for (AbstractDeclClass i : getList()) {
            i.codeGenClass(compiler);
        }
    }

}
