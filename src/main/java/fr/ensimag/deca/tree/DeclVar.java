package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl42
 * @date 01/01/2024
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {

            Type declVarType = type.verifyType(compiler);

            if(declVarType.isVoid()){
                throw new ContextualError("Vous ne pouvez pas declarer une variable de type Void!",getLocation());
            }

            try {
                VariableDefinition VarDefinition = new VariableDefinition(declVarType, getLocation());
                varName.setDefinition(VarDefinition);
                localEnv.declare(varName.getName(), VarDefinition);
            }
            catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("Variable déjà définie", getLocation());
            }

            initialization.verifyInitialization(compiler, declVarType, localEnv, currentClass);
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenDeclVar(DecacCompiler compiler) {
        varName.codeGenInst(compiler);
        compiler.incrD();

        if (initialization instanceof Initialization) {
            initialization.codeGenInit(compiler);
            compiler.addInstruction(new STORE(Register.getR(compiler.getIdReg()), varName.getExpDefinition().getOperand()));
        }
    }
}
