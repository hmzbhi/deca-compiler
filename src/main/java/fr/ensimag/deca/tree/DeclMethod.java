package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.VTable;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

public class DeclMethod extends AbstractDeclMethod {
    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private ListDeclParam params;
    final private AbstractMethodBody body;

    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params, AbstractMethodBody body) {
        Validate.notNull(type);
        Validate.notNull(name);
        Validate.notNull(params);
        Validate.notNull(body);
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
        body.decompile(s);
    }

    @Override
    protected void verifyDeclMethod(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        Type typeReturn = type.verifyType(compiler);
        Signature sig = params.verifyListDeclParam(compiler);
        EnvironmentExp envExpSuper = currentClass.getSuperClass().getMembers();
        ExpDefinition def = envExpSuper.get(name.getName());
        // System.out.println(def + "\n\n\n");

        if (def != null && !def.isMethod()) {
            throw new ContextualError("methode déjà définie dans la classe mère en tant que field", getLocation());
        } else if (def != null && def.isMethod()) {
            MethodDefinition defMethode = def.asMethodDefinition("ce n'est pas une définition de méthode", getLocation());
            Signature sig2 = defMethode.getSignature();
            Type type2 = defMethode.getType();

            if (sig.isSameSignature(sig2) && (type2.sameType(typeReturn) || typeReturn
                    .asClassType("Override impossible, verifiez  le type que renvoie votre fonction", getLocation())
                    .isSubClassOf(type2.asClassType("Override impossible, verifiez  type que renvoie votre fonction",
                            getLocation())))) {
                try {
                    MethodDefinition mDef = new MethodDefinition(type2, getLocation(), sig, defMethode.getIndex());
                    name.setDefinition(defMethode);
                    currentClass.getMembers().declare(name.getName(), mDef);

                    // currentClass.incNumberOfMethods();

                } catch (EnvironmentExp.DoubleDefException e) {
                    throw new ContextualError("methode déjà definie", getLocation());
                }
            } else if (!sig.isSameSignature(sig2)) {
                throw new ContextualError("Override impossible, verifiez la signature de votre fonction", getLocation());
            } else {
                throw new ContextualError("Override impossible, verifiez le type que renvoie votre fonction", getLocation());
            }
        } else if (def == null) {
            try {
                int indexPrevious = currentClass.getNumberOfMethods();
                MethodDefinition mDef = new MethodDefinition(typeReturn, getLocation(), sig, indexPrevious + 1);
                currentClass.incNumberOfMethods();
                currentClass.getMembers().declare(name.getName(), mDef);
                name.setDefinition(mDef);
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("methode deja définie", getLocation());
            }
        }
    }


    protected void verifyDeclMethodBody(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        Type typeVer = type.verifyType(compiler);
        EnvironmentExp envExpParam = new EnvironmentExp(currentClass.getMembers());
        params.verifyListDeclParamBody(compiler, envExpParam, currentClass);
        body.verifyMethodBody(compiler, envExpParam, currentClass, typeVer);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
        params.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, false);
    }

    @Override
    public void codeGenMethodTable(DecacCompiler compiler, Symbol className) {
        MethodDefinition def = name.getMethodDefinition();
        def.setLabel(new Label("code." + className + "." + name.getName()));
        int i = def.getIndex();
        VTable vTable = compiler.getVTable();
        vTable.get(className)[i - 1] = def.getLabel();
    }

    @Override
    public void codeGenMethod(DecacCompiler compiler, Symbol className) {
        compiler.beginBlock();
        // params.codeGenListDeclParam(compiler);
        // body.codeGenMethodBody(compiler);
        // Restauration des registres
        // Sauvegarde des registres (addFirst)
        // TSTO #d (addFirst)
        compiler.addFirst(new Label("code." + className + "." + name.getName()));
        compiler.addFirst("---------- Code de la methode " + name.getName() + " dans la classe " + className.getName() + " ----------");
        compiler.endBlock();
    }

}
