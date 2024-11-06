package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Unary expression.
 *
 * @author gl42
 * @date 01/01/2024
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    public AbstractExpr getOperand() {
        return operand;
    }
    private AbstractExpr operand;
    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }


    protected abstract String getOperatorName();
  
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(" + getOperatorName());
        getOperand().decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

    public BinaryInstruction mnemo(DVal op1, GPRegister op2) {
        return null;
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
}
