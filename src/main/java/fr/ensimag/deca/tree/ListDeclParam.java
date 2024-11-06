package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl42
 * @date 01/01/2024
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {

    @Override
    public void decompile(IndentPrintStream s) {
        boolean first = true;

        for (AbstractDeclParam p : getList()) {
            s.print(first ? "" : ", ");
            p.decompile(s);
            first = false;
        }
    }

    /**
     * Implements non-terminal "list_decl_Param" of [SyntaxeContextuelle] in pass 3
     * 
     * @param compiler     contains the "env_types" attribute
     * @param localEnv
     *                     its "parentEnvironment" corresponds to "env_exp_sup"
     *                     attribute
     *                     in precondition, its "current" dictionary corresponds to
     *                     the "env_exp" attribute
     *                     in postcondition, its "current" dictionary corresponds to
     *                     the "env_exp_r" attribute
     * @param currentClass
     *                     corresponds to "class" attribute (null in the main bloc).
     */
    Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
        Signature sig = new Signature();

        for (AbstractDeclParam param : this.getList()) {
            Type tSig = param.verifyDeclParam(compiler);
            sig.add(tSig);
        }
        
        return sig;
    }

    void verifyListDeclParamBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)throws ContextualError{
        for (AbstractDeclParam param : this.getList()) {
            param.verifyDeclParamBody(compiler, localEnv, currentClass);

        }

    }

    public void codeGenListDeclParam(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

}
