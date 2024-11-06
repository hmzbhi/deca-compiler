package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl42
 * @date 01/01/2024
 */
public class DeclParam extends AbstractDeclParam {

    final private AbstractIdentifier type;
    final private AbstractIdentifier name;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
        Validate.notNull(type);
        Validate.notNull(name);
        this.type = type;
        this.name = name;
    }

    @Override
    protected Type verifyDeclParam(DecacCompiler compiler)
            throws ContextualError {
        Type tVer = type.verifyType(compiler);

        if (tVer.isVoid()) {
            throw new ContextualError("le paramètre ne peut pas être de type Void", getLocation());
        }

        ParamDefinition pDef = new ParamDefinition(tVer, getLocation());
        name.setDefinition(pDef);
        return tVer;
    }

    @Override
    protected void verifyDeclParamBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type typeVer = type.verifyType(compiler);
        ParamDefinition paramDefinition = new ParamDefinition(typeVer, getLocation());
        try {
            type.setType(typeVer);
            name.setDefinition(paramDefinition);
            localEnv.declare(name.getName(), paramDefinition);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Paramètre dèjà utilisé", getLocation());
        }

    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);

    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
    }

    @Override
    public void codeGenDeclParam(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
