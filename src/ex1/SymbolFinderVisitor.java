package ex1;

import ast.*;

public class SymbolFinderVisitor implements Visitor {
    private ex1.Symbol result = null;
    private int lineNumber;
    private String symbolName;
    private boolean isFound = false;

    public SymbolFinderVisitor(String symbolName, int lineNumber) {
        this.lineNumber = lineNumber;
        this.symbolName = symbolName;
    }

    public ex1.Symbol getResult() {
        return result;
    }


    @Override
    public void visit(Program program) {
        for (var classdecl : program.classDecls()) {
            classdecl.accept(this);
            if (isFound) return;
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for (var field : classDecl.fields()) {
            field.accept(this);
            if (isFound) return;
        }

        for (var method : classDecl.methoddecls()) {
            method.accept(this);
            if (isFound) return;
        }
    }

    @Override
    public void visit(MainClass mainClass) {

    }

    @Override
    public void visit(MethodDecl methodDecl) {
        if(methodDecl.name().equals(symbolName) && methodDecl.lineNumber == lineNumber){
            isFound = true;
            result = methodDecl.getSymbolTable().parentMethodLookup(symbolName);
            return;
        }
        
        for (var formalArg : methodDecl.formals()) {
            formalArg.accept(this);
            if (isFound) return;
        }

        for (var decl : methodDecl.vardecls()) {
            decl.accept(this);
            if (isFound) return;
        }
    }

    @Override
    public void visit(FormalArg formalArg) {
        if (formalArg.name().equals(symbolName) && formalArg.lineNumber == lineNumber) {
            result = formalArg.getSymbolTable().varLookup(formalArg.name());
            isFound = true;
        }
    }

    @Override
    public void visit(VarDecl varDecl) {
        if (varDecl.name().equals(symbolName) && varDecl.lineNumber == lineNumber) {
            result = varDecl.getSymbolTable().varLookup(varDecl.name());
            isFound = true;
        }
    }

    @Override
    public void visit(BlockStatement blockStatement) {

    }

    @Override
    public void visit(IfStatement ifStatement) {

    }

    @Override
    public void visit(WhileStatement whileStatement) {

    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {

    }

    @Override
    public void visit(AssignStatement assignStatement) {

    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {

    }

    @Override
    public void visit(AndExpr e) {

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
    public void visit(ArrayAccessExpr e) {

    }

    @Override
    public void visit(ArrayLengthExpr e) {

    }

    @Override
    public void visit(MethodCallExpr e) {

    }

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
    public void visit(ThisExpr e) {

    }

    @Override
    public void visit(NewIntArrayExpr e) {

    }

    @Override
    public void visit(NewObjectExpr e) {

    }

    @Override
    public void visit(NotExpr e) {

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
