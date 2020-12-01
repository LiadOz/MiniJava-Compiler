package ex2;

import ast.MethodDecl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPointer {
    private VtablePointer vtp;
    private int fieldNumber = 8;

    public ClassPointer(String className) {
        vtp = new VtablePointer(className);
    }

    public ClassPointer() {
        vtp = null;
    }

    public int addFunction(String funcName, MethodDecl methodDecl) {
        return vtp.addFunction(funcName, methodDecl);
    }

    public int numberOfFuncs() {
        return vtp.numberOfFuncs();
    }

    public List<VtableEntry> getSortedMethods() {
        return vtp.getSortedMethods();
    }

    public int addField(String fieldName, int size) {
        int ret = fieldNumber;
        fieldNumber += size;
        return ret;
    }

    public int getClassSize() {
        return fieldNumber;
    }

    public ClassPointer subclassPointer(String className) {
        ClassPointer cp = new ClassPointer();
        cp.vtp = vtp.subclassCopy(className);
        cp.fieldNumber = fieldNumber;
        return cp;
    }
}
