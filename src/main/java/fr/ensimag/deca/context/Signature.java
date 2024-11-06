package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl42
 * @date 01/01/2024
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }

    public boolean isSameSignature(Signature otherSignature) {
        if (this.size() != otherSignature.size()) {
            return false;
        }

        for(int i = this.size() - 1; i >= 0; i--) {
            if (!this.paramNumber(i).sameType(otherSignature.paramNumber(i))) {
                return false;
            }
        }

        return true;
    }

}
