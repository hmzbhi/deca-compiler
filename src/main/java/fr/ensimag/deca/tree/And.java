package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
/**
 *
 * @author gl42
 * @date 01/01/2024
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected void code(DecacCompiler compiler, boolean b, Label label) {
        if (b) {
            Label endLabel = new Label("E_Fin_And." + compiler.getLabelNumber());
            
            if (getLeftOperand().dVal() == null && !(getLeftOperand() instanceof AbstractOpCmp)) {
                getLeftOperand().codeGenExpr(compiler);
            } else {
                getLeftOperand().code(compiler, false, endLabel);
            }

            if (getRightOperand().dVal() == null && !(getRightOperand() instanceof AbstractOpCmp)) {
                getRightOperand().codeGenExpr(compiler);
            } else {
                getRightOperand().code(compiler, false, label);
            }
            
            compiler.addLabel(endLabel);
        } else {
            if (getLeftOperand().dVal() == null && !(getLeftOperand() instanceof AbstractOpCmp)) {
                getLeftOperand().codeGenExpr(compiler);
            } else {
                getLeftOperand().code(compiler, false, label);
            }
            
            
            if (getRightOperand().dVal() == null && !(getRightOperand() instanceof AbstractOpCmp)) {
                getRightOperand().codeGenExpr(compiler);
            } else {
                getRightOperand().code(compiler, false, label);
            }
        }
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        Label falseLabel = new Label("and_false." + compiler.getLabelNumber());
        Label endLabel = new Label("end." + compiler.getLabelNumber());
        compiler.incrLabelNumber();

        // <Code(C1 && C2, faux, false)>
        code(compiler, false, falseLabel);
        // LOAD #1 R2
        compiler.addInstruction(new LOAD(1, Register.getR(compiler.getIdReg())));
        // BRA end
        compiler.addInstruction(new BRA(endLabel));
        // false:
        compiler.addLabel(falseLabel);
        // LOAD #0 R2
        compiler.addInstruction(new LOAD(0, Register.getR(compiler.getIdReg())));
        // end:
        compiler.addLabel(endLabel);

    }
}
