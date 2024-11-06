package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

public class Selection extends AbstractLValue {

    final private AbstractExpr expr;
    final private AbstractIdentifier ident;

    public Selection(AbstractExpr expr, AbstractIdentifier ident) {
        this.expr = expr;
        this.ident = ident;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);
        //if(currentClass == null)
        ClassType c_type2 = type2.asClassType("ce n'est pas une classe", getLocation());
        Type f_type = ident.verifyExpr(compiler,c_type2.getDefinition().getMembers(), c_type2.getDefinition());
        FieldDefinition f_def = ident.getDefinition().asFieldDefinition("ce n'est pas un attribut", getLocation());
        if(f_def.getVisibility() == Visibility.PROTECTED){
            if(currentClass==null || !currentClass.getType().isSubClassOf(c_type2)){
                throw new ContextualError("Champ protégé", getLocation());
            }
        }
        this.setType(f_def.getType());

        ident.setDefinition(f_def);

        ident.setType(f_def.getType());
        return f_def.getType();


    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(".");
        ident.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        ident.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        ident.prettyPrint(s, prefix, true);
    }

}
