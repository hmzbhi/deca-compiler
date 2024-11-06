package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.QUO;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.DIV;

/**
 *
 * @author gl42
 * @date 01/01/2024
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    public BinaryInstruction mnemo(DVal op1, GPRegister op2) {
        if (getType().isInt()) {
            return new QUO(op1, op2);
        }
        
        return new DIV(op1, op2);
    }


	@Override
	public void codeGenBOV(DecacCompiler compiler) {
        if (compiler.getCompilerOptions().getCheck()) {
            if (getType().isInt()) {
                compiler.setError(0);
                compiler.addInstruction(new BOV(new Label("zero_division_error")));   
            } else {
                compiler.setError(2);
                compiler.addInstruction(new BOV(new Label("overflow_error")));
            }
        }
    }
}
