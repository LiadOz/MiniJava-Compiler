package ex1;
import ast.Program;

public interface RefactorProgram {
    // Refactor program in place
    public void refactor(Program orig, String originalName, String line, String newName);
}
