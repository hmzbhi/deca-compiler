package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

public class DeclField extends AbstractDeclField {
    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private AbstractInitialization init;

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier name,
            AbstractInitialization init) {
        Validate.notNull(visibility);
        Validate.notNull(name);
        Validate.notNull(init);
        this.visibility = visibility;
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (visibility == Visibility.PROTECTED) {
            s.print(visibility.toString().toLowerCase() + " ");
        }
        
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
        s.println(";");
    }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        Type typeVerified = this.type.verifyType(compiler);

        if (typeVerified.isVoid()) {
            throw new ContextualError("Vous ne pouvez pas déclarer un field de type void ", getLocation());
        }

        ClassDefinition superDef = currentClass.getSuperClass();
        EnvironmentExp envExpSuper = superDef.getMembers();
        ExpDefinition def = envExpSuper.get(name.getName());

        // System.out.println(def + "\n\n\n\n\n");

        if (def != null && !def.isField()) {
            throw new ContextualError("attribut déjà définie dans la classe mère en tant que methode", getLocation());
        } else {
            int indexPrevious = currentClass.getNumberOfFields();
            FieldDefinition fieldDef = new FieldDefinition(typeVerified, getLocation(), visibility, currentClass,
                    indexPrevious + 1);
            try {
                currentClass.getMembers().declare(name.getName(), fieldDef);
                name.setDefinition(fieldDef);
                currentClass.incNumberOfFields();
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("field déjà défini", getLocation());
            }
        }

        // init.verifyInitialization(compiler, typeVerified, currentClass.getMembers(),
        // currentClass);
    }
    protected void verifyDeclFieldBody(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {

        Type typeVerified = this.type.verifyType(compiler);
        init.verifyInitialization(compiler, typeVerified, currentClass.getMembers(), currentClass);

        //name.setDefinition();
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
        init.iter(f);
    }

    protected Visibility getVisibilty() {
        return this.visibility;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        init.prettyPrint(s, prefix, false);
    }

    @Override
    String prettyPrintNode() {
        return "[visibilty=" + getVisibilty().toString() + "] " + super.prettyPrintNode();
    }

    @Override
    protected void codeGenDeclField(DecacCompiler compiler) {
        // throw new UnsupportedOperationException("not yet implemented");
    }

}
