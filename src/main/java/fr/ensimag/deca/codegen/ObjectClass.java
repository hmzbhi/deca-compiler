package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

/**
 * ObjectClass
 */
public class ObjectClass {

    public static void codeGenClass(DecacCompiler compiler){
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("                   Classe Object");
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("---------- Initialisation des champs de Object ----------");
        compiler.addLabel(new Label("init.Object"));
        compiler.addInstruction(new RTS());
        compiler.addComment("---------- Code de la méthode equals dans la classe Object ----------");
        compiler.addLabel(new Label("code.Object.equals"));
        // compiler.addInstruction(new TSTO(2));

        // if (compiler.getCompilerOptions().getCheck()) {
        //     compiler.addInstruction(new BOV(new Label("stack_overflow_error")));
        // }
        
        // compiler.addComment("Sauvegarde des registres");
        // compiler.addInstruction(new PUSH(Register.getR(2)));
        // compiler.addInstruction(new PUSH(Register.getR(3)));
        // compiler.addComment("Corps de la méthode");
        // compiler.addComment("TODO: this == object");
        // compiler.addInstruction(new BEQ(new Label("egalite")));
        // compiler.addInstruction(new LOAD(0, Register.R0));
        // compiler.addInstruction(new BRA(new Label("fin.Object.equals")));
        // compiler.addLabel(new Label("egalite"));
        // compiler.addInstruction(new LOAD(1, Register.R0));
        // compiler.addInstruction(new BRA(new Label("fin.Object.equals")));
        // compiler.addInstruction(new WSTR("Erreur : sortie de la méthode Object.equals sans return"));
        // compiler.addInstruction(new WNL());
        // compiler.addLabel(new Label("fin.Object.equals"));
        // compiler.addComment("Restauration des registres");
        // compiler.addInstruction(new POP(Register.getR(2)));
        // compiler.addInstruction(new POP(Register.getR(3)));
        compiler.addInstruction(new RTS());
    }

    public static void codeGenMethodTable(DecacCompiler compiler) {
        compiler.addComment("Construction de la table des methodes de Object");
        
        Symbol name = compiler.createSymbol("Object");
        compiler.getClassAdresses().put(name, new RegisterOffset(compiler.getD(), Register.GB));

        compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(0)));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.getD(), Register.GB)));
        compiler.incrD();

        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.getR(0)));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.getD(), Register.GB)));
        compiler.incrD();
    }
}