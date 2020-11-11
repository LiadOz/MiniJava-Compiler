package ex1;

import ast.AstNode;
import ast.ClassDecl;
import ast.Program;

public class RefactorMethod implements RefactorProgram {
    @Override
    public void refactor(Program orig, String originalName, String line, String newName) {
        ClassMapping classMap = new ClassMapping();
        SymbolTableVisitor symbolTableBuilder = new SymbolTableVisitor(new SymbolTable(), classMap);
        orig.accept(symbolTableBuilder);

        SymbolFinderVisitor symbolFinder = new SymbolFinderVisitor(originalName, Integer.parseInt(line));
        orig.accept(symbolFinder);

        Symbol replaceSymbol = symbolFinder.getResult();
        orig.accept(new RenameMethodVisitor(replaceSymbol, classMap, newName));
    }
}
