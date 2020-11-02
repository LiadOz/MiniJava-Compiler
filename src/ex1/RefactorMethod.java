package ex1;

import ast.AstNode;
import ast.Program;

public class RefactorMethod implements RefactorProgram {
    @Override
    public void refactor(Program orig, String originalName, String line, String newName) {
        LineVisitor linev = new LineVisitor(Integer.getInteger(line)); // TODO: find the scope of the var
        orig.accept(linev);
        AstNode scope = linev.getResult();
        RenameVarVisitor rename = new RenameVarVisitor(originalName, newName); // TODO: rename the var
        scope.accept(rename);
    }
}
