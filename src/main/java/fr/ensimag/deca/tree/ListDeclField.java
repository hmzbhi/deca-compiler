package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.RTS;

import org.apache.log4j.Logger;

public class ListDeclField extends TreeList<AbstractDeclField> {
    private static final Logger LOG = Logger.getLogger(ListDeclField.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : getList()) {
            f.decompile(s);
        }
    }


    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListFieldMembers(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError {
        // EnvironmentExp envExpR = new EnvironmentExp(null);

        for (AbstractDeclField declfield : this.getList()) {
            declfield.verifyDeclField(compiler, currentClass);
            // envExpR.declare();
        }
        
        // envExpR.
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListFieldBody(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclField declfield : this.getList()) {
            declfield.verifyDeclFieldBody(compiler, currentClass);
            // envExpR.declare();
        }

    }


    public void codeGenListField(DecacCompiler compiler, Symbol className) {
        compiler.addComment("---------- Initialisation des champs de la classe " + className + " ----------");
        compiler.addLabel(new Label("init." + className));
        for (AbstractDeclField f : getList()) {
            f.codeGenDeclField(compiler);
        }
        compiler.addInstruction(new RTS());
    }
}
