package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl42
 * @date 01/01/2024
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);

        if (!type.isBoolean()) {
            throw new ContextualError("Le type attendu après not est boolean!", getLocation());
        }

        setType(type);
        return type;
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }

    @Override
    protected void code(DecacCompiler compiler, boolean b, Label label) {
        if (getOperand().dVal() == null && !(getOperand() instanceof AbstractOpCmp)) {
            getOperand().codeGenExpr(compiler);
        } else {
            getOperand().code(compiler, !b, label);
        }
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler) {
        Label falseLabel = new Label("not_false." + compiler.getLabelNumber());
        Label endLabel = new Label("end." + compiler.getLabelNumber());
        compiler.incrLabelNumber();

        // <Code(!C, faux, false)>
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
