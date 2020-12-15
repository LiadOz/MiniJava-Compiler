package ex1;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Symbol> varEntries;
    private Map<String, Symbol> methodEntries;
    private SymbolTable parent = null;

    public SymbolTable() {
        varEntries = new HashMap<String, Symbol>();
        methodEntries = new HashMap<String, Symbol>();
    }
    public SymbolTable(SymbolTable parent) {
        this();
        this.parent = parent;
    }

    public SymbolTable getParent(){
        return parent;
    }

    public void addVar(String id, Symbol s){
        if (varEntries.containsKey(id)) {
            throw new RuntimeException(id + " already in symbol table");
        }
        varEntries.put(id, s);
    }

    public Symbol removeVar(String id) {
        if (!varEntries.containsKey(id)) {
            throw new RuntimeException(id + " not in symbol table");
        }
        return varEntries.remove(id);
    }

    public void updateVarId(String id) {
        Symbol s = removeVar(id);
        s.setId(id);
        addVar(id, s);
    }

    public void addMethod(String id, Symbol s){
        if (methodEntries.containsKey(id)) {
            throw new RuntimeException(id + " already in symbol table");
        }
        methodEntries.put(id, s);
    }

    public Symbol removeMethod(String id) {
        if (!methodEntries.containsKey(id)) {
            throw new RuntimeException(id + " not in symbol table");
        }
        return methodEntries.remove(id);
    }

    public void updateMethodId(String id) {
        Symbol s = removeMethod(id);
        s.setId(id);
        addMethod(id, s);
    }

    public Symbol varLookup(String id){
        Symbol ret = varEntries.get(id);
        if (ret == null){
            if (parent != null){
                return parent.varLookup(id);
            }
            else {
                throw new RuntimeException(id + " not in symbol table");
            }
        }
        return ret;
    }

    public Symbol methodLookup(String id){
        Symbol ret = methodEntries.get(id);
        if (ret == null){
            if (parent != null){
                return parent.methodLookup(id);
            }
            else {
                throw new RuntimeException(id + " not in symbol table");
            }
        }
        return ret;
    }

    // finds the upmost method in class tree
    public Symbol parentMethodLookup(String id) {
        Symbol ret = methodLookup(id);
        try {
            ret = parent.parentMethodLookup(id);
        }
        catch (RuntimeException e) {
        }
        return ret;

    }

    public void printTable(){
        for (var value : methodEntries.values())
            System.out.println(value.getId() + " " + value.getKind() + " " + value.getDecl());
        for (var value : varEntries.values())
            System.out.println(value.getId() + " " + value.getKind() + " " + value.getDecl());
    }
}
