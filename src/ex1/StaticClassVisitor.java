package ex1;

import ast.*;

public class StaticClassVisitor implements Visitor {
    public static final String THIS_STRING = "1CURRENT"; // 1 is because it can't be used in real class name
    private String className = null;

    public String getResult() {
        return className;
    }

    public void clearResult() {
        className = null;
    }

    @Override
    public void visit(IntegerLiteralExpr e) {
        className = "int";
    }
    @Override
    public void visit(IdentifierExpr e) {
        className = e.getSymbolTable().varLookup(e.id()).getDecl();
        // System.out.println(e.id() + " " + className);
    }

    @Override
    public void visit(ThisExpr e) {
        className = THIS_STRING;
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        className = "int[]";
    }

    @Override
    public void visit(NewObjectExpr e) {
        className = e.classId();
    }
    @Override
    public void visit(TrueExpr e) { className = "boolean";}
    @Override
    public void visit(FalseExpr e) { className = "boolean";}
    @Override
    public void visit(Program program) { }
    @Override
    public void visit(ClassDecl classDecl) { }
    @Override
    public void visit(MainClass mainClass) { }
    @Override
    public void visit(MethodDecl methodDecl) { }
    @Override
    public void visit(FormalArg formalArg) { }
    @Override
    public void visit(VarDecl varDecl) { }
    @Override
    public void visit(BlockStatement blockStatement) { }
    @Override
    public void visit(IfStatement ifStatement) { }
    @Override
    public void visit(WhileStatement whileStatement) { }
    @Override
    public void visit(SysoutStatement sysoutStatement) { }
    @Override
    public void visit(AssignStatement assignStatement) { }
    @Override
    public void visit(AssignArrayStatement assignArrayStatement) { }
    @Override
    public void visit(AndExpr e) { }
    @Override
    public void visit(LtExpr e) { }
    @Override
    public void visit(AddExpr e) { }
    @Override
    public void visit(SubtractExpr e) { }
    @Override
    public void visit(MultExpr e) { }
    @Override
    public void visit(ArrayAccessExpr e) { }
    @Override
    public void visit(ArrayLengthExpr e) { }
    @Override
    public void visit(MethodCallExpr e) { }
    @Override
    public void visit(NotExpr e) { }
    @Override
    public void visit(IntAstType t) { }
    @Override
    public void visit(BoolAstType t) { }
    @Override
    public void visit(IntArrayAstType t) { }
    @Override
    public void visit(RefType t) { }
}
