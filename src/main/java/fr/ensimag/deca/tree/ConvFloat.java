package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl42
 * @date 01/01/2024
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        if (getOperand().dVal() != null) {
            compiler.addInstruction(mnemo(getOperand().dVal(), Register.getR(compiler.getIdReg())));
        } else {
            getOperand().codeGenExpr(compiler);
            compiler.addInstruction(mnemo(Register.getR(compiler.getIdReg()), Register.getR(compiler.getIdReg())));
        }
    }

	@Override
	public BinaryInstruction mnemo(DVal op1, GPRegister op2) {
		return new FLOAT(op1, op2);
	}
}
