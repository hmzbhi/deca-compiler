package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.MUL;

/**
 * @author gl42
 * @date 01/01/2024
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    public BinaryInstruction mnemo(DVal op1, GPRegister op2) {
        return new MUL(op1, op2);
    }


    @Override
    public void codeGenBOV(DecacCompiler compiler) {
        if (compiler.getCompilerOptions().getCheck()) {
            if (getType().isFloat()) {
                compiler.setError(2);
                compiler.addInstruction(new BOV(new Label("overflow_error")));
            }
        }
    }
}
