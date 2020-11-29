package ex2;

import ast.*;
import ex1.ClassMapping;

public class CompileVisitor implements Visitor {
    private final StringBuilder builder = new StringBuilder();
    private final ClassMapping classMapping;

    public CompileVisitor(ClassMapping classMapping) {
        this.classMapping = classMapping;
    }
    public String getString() {
        return builder.toString();
    }

    @Override
    public void visit(Program program) {

    }


    @Override
    public void visit(MainClass mainClass) {

    }

    // OOP
    @Override
    public void visit(ClassDecl classDecl) {
    }

    @Override
    public void visit(MethodDecl methodDecl) {

    }

    @Override
    public void visit(FormalArg formalArg) {

    }

    @Override
    public void visit(VarDecl varDecl) {
        // same as formal arg ??
    }

    @Override
    public void visit(MethodCallExpr e) {

    }

    @Override
    public void visit(ThisExpr e) {

    }

    @Override
    public void visit(NewObjectExpr e) {

    }

    // IF and JUMPS
    @Override
    public void visit(BlockStatement blockStatement) {

    }

    @Override
    public void visit(IfStatement ifStatement) {

    }

    @Override
    public void visit(WhileStatement whileStatement) {

    }
    // ARRAYS
    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {

    }

    @Override
    public void visit(ArrayAccessExpr e) {
    }

    @Override
    public void visit(ArrayLengthExpr e) {

    }

    @Override
    public void visit(NewIntArrayExpr e) {

    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {

    }

    @Override
    public void visit(AssignStatement assignStatement) {
        // define temp assign from rv
    }

    // Exp and trees
    @Override
    public void visit(AndExpr e) {
        // implement short circuit
    }

    @Override
    public void visit(LtExpr e) {

    }

    @Override
    public void visit(AddExpr e) {

    }

    @Override
    public void visit(SubtractExpr e) {

    }

    @Override
    public void visit(MultExpr e) {

    }

    @Override
    public void visit(NotExpr e) {

    }

    // SIMPLE
    @Override
    public void visit(IntegerLiteralExpr e) {

    }

    @Override
    public void visit(TrueExpr e) {

    }

    @Override
    public void visit(FalseExpr e) {

    }

    @Override
    public void visit(IdentifierExpr e) {

    }


    @Override
    public void visit(IntAstType t) {

    }

    @Override
    public void visit(BoolAstType t) {

    }

    @Override
    public void visit(IntArrayAstType t) {

    }

    @Override
    public void visit(RefType t) {

    }
}
