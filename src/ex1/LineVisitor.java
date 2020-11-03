package ex1;

import ast.*;

public class LineVisitor implements Visitor {
    private AstNode result = null;
    private int lineNumber;
    private String varName;
    private boolean isFound = false;

    public LineVisitor(String varName, int lineNumber) {
        this.lineNumber = lineNumber;
        this.varName = varName;
    }

    public AstNode getResult() {
        return result;
    }

    @Override
    public void visit(Program program) {
        for (var classdecl : program.classDecls()) {
            classdecl.accept(this);
            if (this.isFound) {
                return;
            }
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for(var field: classDecl.fields()){
            field.accept(this);
            if(this.isFound){
                this.result = classDecl;
                return;
            }
        }

        for(var method: classDecl.methoddecls()){
            method.accept(this);
            if(this.isFound){
                return;
            }
        }



    }

    @Override
    public void visit(MainClass mainClass) {

    }

    @Override
    public void visit(MethodDecl methodDecl) {
        for(var formalArg: methodDecl.formals()){
            formalArg.accept(this);
            if(this.isFound){
                this.result = methodDecl;
                return;
            }
        }

        for(var decl: methodDecl.vardecls()){
            decl.accept(this);
            if(this.isFound){
                this.result = methodDecl;
                return;
            }
        }
    }

    @Override
    public void visit(FormalArg formalArg) {
        if(formalArg.name().equals(this.varName) && formalArg.lineNumber == this.lineNumber){
            this.isFound = true;
        }
    }

    @Override
    public void visit(VarDecl varDecl) {
        if(varDecl.name().equals(this.varName) && varDecl.lineNumber == this.lineNumber){
            this.isFound = true;
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
