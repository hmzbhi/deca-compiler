package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Variable declaration
 *
 * @author gl42
 * @date 01/01/2024
 */

public class MethodBody extends AbstractMethodBody {
    private ListDeclVar decls;
    private ListInst insts;

    public MethodBody(ListDeclVar decls, ListInst insts) {
        Validate.notNull(decls);
        Validate.notNull(insts);
        this.decls = decls;
        this.insts = insts;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        decls.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, false);
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type typeReturn)
            throws ContextualError {
        decls.verifyListDeclVariable(compiler, localEnv, currentClass);
        insts.verifyListInst(compiler, localEnv, currentClass,typeReturn);
    }

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        decls.iter(f);
        insts.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println(" {");
        s.indent();
        decls.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

}
