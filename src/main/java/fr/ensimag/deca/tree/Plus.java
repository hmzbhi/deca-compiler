package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;

/**
 * @author gl42
 * @date 01/01/2024
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
 

    @Override
    protected String getOperatorName() {
        return "+";
    }

    @Override
    public BinaryInstruction mnemo(DVal op1, GPRegister op2) {
        return new ADD(op1, op2);
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
