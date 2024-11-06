package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Paramiable declaration
 *
 * @author gl42
 * @date 01/01/2024
 */
public abstract class AbstractDeclParam extends Tree {

    /**
     * Implements non-terminal "decl_Param" of [SyntaxeContextuelle] in pass 3
     * 
     * @param compiler     contains "env_types" attribute
     * @param localEnv
     *                     its "parentEnvironment" corresponds to the "env_exp_sup"
     *                     attribute
     *                     in precondition, its "current" dictionary corresponds to
     *                     the "env_exp" attribute
     *                     in postcondition, its "current" dictionary corresponds to
     *                     the synthetized attribute
     * @param currentClass
     *                     corresponds to the "class" attribute (null in the main
     *                     bloc).
     */
    protected abstract Type verifyDeclParam(DecacCompiler compiler)
            throws ContextualError;
    protected abstract void verifyDeclParamBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
        throws ContextualError;


    protected abstract void codeGenDeclParam(DecacCompiler compiler);


}
