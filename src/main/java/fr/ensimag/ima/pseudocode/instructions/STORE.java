package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;

/**
 * @author Ensimag
 * @date 01/01/2024
 */
public class STORE extends BinaryInstruction {
    public STORE(Register op1, DVal dVal) {
        super(op1, dVal);
    }
}
