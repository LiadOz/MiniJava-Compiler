package ex1;

import ast.AstNode;
import ast.Program;

public class RefactorVariable implements RefactorProgram {
    @Override
    public void refactor(Program orig, String originalName, String line, String newName) {
        ClassMapping classMap = new ClassMapping();
        SymbolTableVisitor symbolTableBuilder = new SymbolTableVisitor(new SymbolTable(), classMap);
        orig.accept(symbolTableBuilder);
        ex1.SymbolFinderVisitor finder = new ex1.SymbolFinderVisitor(originalName, Integer.parseInt(line));
        orig.accept(finder);
        ex1.Symbol targetSymbol = finder.getResult();
        RenameVarVisitor rename = new RenameVarVisitor(newName, targetSymbol);
        orig.accept(rename);
    }
}
