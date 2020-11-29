package ex2;

import ast.Program;
import ex1.ClassMapping;
import ex1.RenameVarVisitor;
import ex1.SymbolTable;
import ex1.SymbolTableVisitor;

public class ProgramCompiler {
    public String compile(Program prog) {
        ClassMapping classMap = new ClassMapping();
        SymbolTableVisitor symbolTableBuilder = new SymbolTableVisitor(new SymbolTable(), classMap);
        prog.accept(symbolTableBuilder);
        CompileVisitor compVisitor = new CompileVisitor(classMap);
        prog.accept(compVisitor);
        return compVisitor.getString();
    }
}
