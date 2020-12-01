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

}
