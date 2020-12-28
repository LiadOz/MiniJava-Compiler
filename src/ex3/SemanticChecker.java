package ex3;

import ast.Program;
import ex1.ClassMapping;
import ex1.SymbolTable;
import ex1.SymbolTableVisitor;

public class SemanticChecker {

    public String check(Program program){
        try {
            program.accept(new InitialCheckerVisitor()); // in order to use SymbolTableVisitor
            ClassMapping classMap = new ClassMapping();
            SymbolTableVisitor symbolTableBuilder = new SymbolTableVisitor(new SymbolTable(), classMap);
            program.accept(symbolTableBuilder);
            program.accept(new ClassCheckerVisitor(classMap));
            program.accept(new TypeCheckerVisitor(classMap));
            program.accept(new VariableInitVisitor());
            return "OK\n";
        } catch (Exception e) {
            // System.out.println(e.getMessage()); // for checking if cause is correct
            return "ERROR\n";
        }
    }
}
