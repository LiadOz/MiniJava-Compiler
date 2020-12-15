package ex1;


import java.util.HashMap;
import java.util.Map;

public class ClassMapping {
    private final Map<String, SymbolTable> mapping;

    public ClassMapping() {
        mapping = new HashMap<String, SymbolTable>();
    }

    public void add(String classId, SymbolTable table) {
        if (mapping.containsKey(classId))
            throw new RuntimeException(classId + " already mapped");
        mapping.put(classId, table);
    }

    public SymbolTable get(String classId) {
        if (!mapping.containsKey(classId))
            throw new RuntimeException(classId + " not mapped");
        return mapping.get(classId);
    }

    // Needs testing
    public boolean isValidSubclass(String father, String son){
        var currentTable = mapping.get(son);
        var targetTable = mapping.get(father);

        while(currentTable != null){
            if(currentTable == targetTable) return true;
            currentTable = currentTable.getParent();
        }
        return false;
    }

}
