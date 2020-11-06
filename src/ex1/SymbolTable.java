package ex1;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Symbol> varEntries;
    private Map<String, Symbol> methodEntries;
    private SymbolTable parent = null;

    public SymbolTable(){
        varEntries = new HashMap<String, Symbol>();
        methodEntries = new HashMap<String, Symbol>();
    }
    public SymbolTable(SymbolTable parent) {
        this();
        this.parent = parent;
    }

    public void addVar(String id, Symbol s){
        if (varEntries.containsKey(id)) {
            throw new RuntimeException(id + " already in symbol table");
        }
        varEntries.put(id, s);
    }

    public void addMethod(String id, Symbol s){
        if (methodEntries.containsKey(id)) {
            throw new RuntimeException(id + " already in symbol table");
        }
        methodEntries.put(id, s);
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
}
