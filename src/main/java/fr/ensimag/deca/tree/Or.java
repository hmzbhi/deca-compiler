package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl42
 * @date 01/01/2024
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }


    @Override
    protected void code(DecacCompiler compiler, boolean b, Label label) {
        if (b) {
            if (getLeftOperand().dVal() == null && !(getLeftOperand() instanceof AbstractOpCmp)) {
                getLeftOperand().codeGenExpr(compiler);
            } else {
                getLeftOperand().code(compiler, true, label);
            }
            
            
            if (getRightOperand().dVal() == null && !(getRightOperand() instanceof AbstractOpCmp)) {
                getRightOperand().codeGenExpr(compiler);
            } else {
                getRightOperand().code(compiler, true, label);
            }
        } else {
            Label endLabel = new Label("E_Fin_Or." + compiler.getLabelNumber());

            if (getLeftOperand().dVal() == null && !(getLeftOperand() instanceof AbstractOpCmp)) {
                getLeftOperand().codeGenExpr(compiler);
            } else {
                getLeftOperand().code(compiler, true, endLabel);
            }

            
            if (getRightOperand().dVal() == null && !(getRightOperand() instanceof AbstractOpCmp)) {
                getRightOperand().codeGenExpr(compiler);
            } else {
                getRightOperand().code(compiler, false, label);
            }
            
            compiler.addLabel(endLabel);
        }
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        Label trueLabel = new Label("or_true." + compiler.getLabelNumber());
        Label endLabel = new Label("end." + compiler.getLabelNumber());
        compiler.incrLabelNumber();

        // <Code(C, vrai, true)>
        code(compiler, true, trueLabel);

        // LOAD #0 R2
        compiler.addInstruction(new LOAD(0, Register.getR(compiler.getIdReg())));
        // BRA end
        compiler.addInstruction(new BRA(endLabel));

        // true:
        compiler.addLabel(trueLabel);
        // LOAD #1 R2
        compiler.addInstruction(new LOAD(1, Register.getR(compiler.getIdReg())));
        // end:
        compiler.addLabel(endLabel);
    }
}
