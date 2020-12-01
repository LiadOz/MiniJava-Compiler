package ex2;

import ast.MethodDecl;

import java.util.*;

public class VtablePointer {
    private final String className;
    private final Map<String, VtableEntry> funcToEntry;

    public VtablePointer(String className) {
        this.className = className;
        this.funcToEntry = new HashMap<>();
    }

    public int addFunction(String funcName, MethodDecl methodDecl) {
        VtableEntry entry = funcToEntry.get(funcName);
        if (entry == null) { // if it doesn't exist add a new entry
            entry = new VtableEntry(funcToEntry.size(), className, funcName, methodDecl);
            funcToEntry.put(funcName, entry);
        }
        else { // if it exists then override the class name
            entry.setClassName(className);
        }
        return entry.getIndex();
    }

    public int numberOfFuncs() {
        return funcToEntry.size();
    }

    public VtablePointer subclassCopy(String subclassName) { // creates a copy of contents to be used in subclass
        VtablePointer vtp = new VtablePointer(subclassName);
        for (var vte : funcToEntry.values()) {
            VtableEntry entry = new VtableEntry(vte.getIndex(), vte.getClassName(),
                    vte.getFuncName(), vte.getMethodDecl());
            vtp.funcToEntry.put(vte.getFuncName(), entry);
        }
        return vtp;
    }

    public List<VtableEntry> getSortedMethods() {
        List<VtableEntry> ret = new ArrayList<VtableEntry>(funcToEntry.size());
        for (VtableEntry vte : funcToEntry.values()) {
            ret.add(vte.getIndex(), vte);
        }
        return ret;
    }
}
