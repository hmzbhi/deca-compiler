package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

public class MethodCall extends AbstractExpr {

    final private AbstractExpr expr;
    final private AbstractIdentifier ident;
    final private ListExpr list;

    public MethodCall(AbstractExpr expr, AbstractIdentifier ident, ListExpr list) {
        this.expr = expr;
        this.ident = ident;
        this.list = list;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);
        ClassType cType2 = type2.asClassType("Vous ne pouvez pas appelez la méthode sur ce type", getLocation());

        // je sais si j en ai vraiment besoin , je sais pas si ca me renvoie cce que je
        // pense vrmnt
        ExpDefinition def = cType2.getDefinition().getMembers().get(ident.getName());
        if(def == null){
            def = cType2.getDefinition().getSuperClass().getMembers().get(ident.getName());
        }

        if (def == null){
            throw new ContextualError("Methode " + ident.getName() + " non définie dans la classe "
                    + cType2.getDefinition().getType().getName().toString(), getLocation());
        }
        else  {
            MethodDefinition mDefVrai = def.asMethodDefinition("Ce n'est pas une méthode ", getLocation());
            Signature sigVrai = mDefVrai.getSignature();
            int len = sigVrai.size();
            if (list.getList().size() != len) {
                throw new ContextualError("Verifiez le nombre de parametre de la fonctions " + ident.getName(),
                        getLocation());
            } else {
                for (int i = 0; i < len; i++) {
                    // Type typeParam = list.getList().get(i).verifyExpr(compiler, localEnv,
                    // currentClass);
                    Type expectedType = sigVrai.paramNumber(i);
                    list.getList().get(i).verifyRValue(compiler, localEnv, currentClass, expectedType);

                }
                setType(mDefVrai.getType());
                ident.setType(mDefVrai.getType());
                ident.setDefinition(mDefVrai);
                ident.setType(mDefVrai.getType());

                return mDefVrai.getType();

            }
        }
        // System.out.println(mDef.getSignature().isSameSignature(mDefVrai.getSignature()));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (!expr.isImplicit()) {
            expr.decompile(s);
            s.print(".");
        }
        
        ident.decompile(s);
        s.print("(");
        list.decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        ident.iter(f);
        list.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        ident.prettyPrint(s, prefix, false);
        list.prettyPrint(s, prefix, true);
    }

}
