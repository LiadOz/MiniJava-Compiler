package ex1;

import ast.AstNode;
import ast.Program;

public class RefactorVariable implements RefactorProgram {
    @Override
    public void refactor(Program orig, String originalName, String line, String newName) {
        ex1.SymbolFinderVisitor finder = new ex1.SymbolFinderVisitor(originalName, Integer.parseInt(line)); // TODO: find the scope of the var
        orig.accept(finder);
        ex1.Symbol targetSymbol = finder.getResult();
        RenameVarVisitor rename = new RenameVarVisitor(originalName, newName); // TODO: rename the var
    }
}
