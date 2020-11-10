package ex1;


import ast.Program;

public class RefactorMethod implements RefactorProgram {
    @Override
    public void refactor(Program orig, String originalName, String line, String newName) {
        // // find the class containing the method
        // LineVisitor lVisitor = new LineVisitor(Integer.parseInt(line));
        // orig.accept(lVisitor);
        // AstNode realscope = lVisitor.getResult();
        // ClassDecl scope = new ClassDecl(); // this line is to be replaced with the real scope

        // // find all subclasses
        // ValidSubclass vs = new ValidSubclass(scope);
        // vs.initProg(orig);

        // // rename the method in the class and the subclasses
        // RenameMethodVisitor rename = new RenameMethodVisitor(originalName, newName, vs);
        // // rename the method in every variable by static type that fits the class and subclasses
        // // this includes by "this." inside the classes and by "(parent)a."
        ClassMapping classMap = new ClassMapping();
        SymbolTableVisitor symbolTableBuilder = new SymbolTableVisitor(new SymbolTable(), classMap);
        orig.accept(symbolTableBuilder);
    }
}
