package ex1;


import ex3.SemanticException;

import java.util.HashMap;
import java.util.Map;

public class ClassMapping {
    private final Map<String, SymbolTable> mapping;

    public ClassMapping() {
        mapping = new HashMap<String, SymbolTable>();
    }

    public void add(String classId, SymbolTable table) {
        if (mapping.containsKey(classId))
            throw new SemanticException(classId + " already mapped");
        mapping.put(classId, table);
    }

    // returns null if class is a simple type, if class doesn't exist throws error
    public SymbolTable get(String classId) {
        if (isSimpleType(classId))
            return null;
        if (!mapping.containsKey(classId))
            throw new SemanticException(classId + " not mapped");
        return mapping.get(classId);
    }

    // Needs testing
    public boolean isValidSubclass(String father, String son){
        if (isSimpleType(father) || isSimpleType(son)) // handle case of int, int[], boolean
            return father.equals(son);

        var currentTable = mapping.get(son);
        var targetTable = mapping.get(father);

        while (currentTable != null){
            if (currentTable == targetTable) return true;
            currentTable = currentTable.getParent();
        }
        return false;
    }

    private static boolean isSimpleType(String className) {
        return className.equals("int") || className.equals("int[]") || className.equals("boolean");
    }

}
