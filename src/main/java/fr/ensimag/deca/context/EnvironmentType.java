package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl42
 * @date 01/01/2024
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {

        envTypes = new HashMap<Symbol, TypeDefinition>();

        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);

        Symbol objectSymb = compiler.createSymbol("Object");
        OBJECT = new ClassType(objectSymb, Location.BUILTIN, null);

        Signature signature = new Signature();
        signature.add(OBJECT);
        MethodDefinition method = new MethodDefinition(BOOLEAN, Location.BUILTIN, signature, 1);
        Symbol equalsSymb = compiler.createSymbol("equals");

        try {
            OBJECT.getDefinition().getMembers().declare(equalsSymb, (ExpDefinition) method);
            OBJECT.getDefinition().incNumberOfMethods();
        } catch (EnvironmentExp.DoubleDefException e) {
            // rien a verifier;
        }

        envTypes.put(objectSymb, OBJECT.getDefinition());
    }
    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }
    private final Map<Symbol, TypeDefinition> envTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public Map<Symbol, TypeDefinition> getEnvTypes() {
        return envTypes;
    }

    public void declare_type(Symbol name, ClassDefinition def) throws EnvironmentType.DoubleDefException {
        if (envTypes.containsKey(name)) {
            throw new EnvironmentType.DoubleDefException();
        }
        envTypes.put(name, def);
    }

    public final VoidType VOID;
    public final IntType INT;
    public final FloatType FLOAT;
    public final StringType STRING;
    public final BooleanType BOOLEAN;
    public final ClassType OBJECT;
}
