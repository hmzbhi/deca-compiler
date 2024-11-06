package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

public class Cast extends AbstractExpr {
    private AbstractIdentifier ident;
    private AbstractExpr expr;

    public Cast(AbstractIdentifier ident, AbstractExpr expr) {
        this.ident = ident;
        this.expr = expr;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //throw new UnsupportedOperationException("not yet implemented");
        Type type = ident.verifyType(compiler);
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);

        if(type.isClass() && type2.isClass() )
        {
            if (type2.asClassType("", getLocation()).isSubClassOf(type.asClassType("", getLocation()))) {
                setType(type.asClassType("", getLocation()));
                return type.asClassType("", getLocation());
            }
            else if(type.asClassType("", getLocation()).isSubClassOf(type2.asClassType("", getLocation()))) {
                setType(type.asClassType("", getLocation()));
                return type.asClassType("", getLocation());
        }
        throw new ContextualError("Cast impossible", getLocation());
        }
        else if(type.isFloat() && type2.isInt()) {
            setType(compiler.environmentType.FLOAT);
            return compiler.environmentType.FLOAT;
        }
        else if(type.isInt() && type2.isFloat()){
            setType(compiler.environmentType.INT);
            return compiler.environmentType.INT;

        }
        throw new ContextualError("Cast impossible", getLocation());

    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        ident.decompile(s);
        s.print(") (");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        ident.iter(f);
        expr.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        ident.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

}
