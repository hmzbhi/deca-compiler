package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

public class New extends AbstractExpr {
    private AbstractIdentifier ident;

    public New(AbstractIdentifier ident) {
        this.ident = ident;
    }

    public AbstractIdentifier getIdent() {
        return ident;
    }

    public void setExpr(AbstractIdentifier ident) {
        this.ident = ident;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeVer = ident.verifyType(compiler);
        if (!typeVer.isClass()) {
            throw new ContextualError("Vous ne pouvez pas appeler New sur ce type", getLocation());
        } else {
            setType(typeVer);
            return typeVer;
        }

    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        ident.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        ident.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        ident.prettyPrint(s, prefix, true);
    }

}
