package ex1;

import ast.*;

public class RenameVarVisitor implements Visitor {
    private String oldName;
    private String newName;
    private Symbol symbol;

    public RenameVarVisitor(String oldName, String newName, Symbol symbol){
        this.oldName = oldName;
        this.newName = newName;
        this.symbol = symbol;
    }
    @Override
    public void visit(Program program) {
        for(var classDecl : program.classDecls()) classDecl.accept(this);
        program.mainClass().accept(this);
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for(var field: classDecl.fields()) field.accept(this);
        for(var method: classDecl.methoddecls()) method.accept(this);
    }

    @Override
    public void visit(MainClass mainClass) {
        mainClass.mainStatement().accept(this);
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        for(var formal: methodDecl.formals()) formal.accept(this);
        for(var varDecl: methodDecl.vardecls()) varDecl.accept(this);
        methodDecl.ret().accept(this);
    }

    @Override
    public void visit(FormalArg formalArg) {
        if(formalArg.name().equals(oldName)){
            var correspondingSymbol = formalArg.getSymbolTable().varLookup(oldName);
            if(correspondingSymbol == symbol) {
                formalArg.setName(newName);
            }
        }
    }

    @Override
    public void visit(VarDecl varDecl) {
        if(varDecl.name().equals(oldName)){
           var correspondingSymbol = varDecl.getSymbolTable().varLookup(oldName);
           if(correspondingSymbol == symbol){
               varDecl.setName(newName);
           }
        }
    }

    @Override
    public void visit(BlockStatement blockStatement) {
        for(var statement: blockStatement.statements()) statement.accept(this);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.cond().accept(this);
        ifStatement.thencase().accept(this);
        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);
        whileStatement.body().accept(this);
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
            sysoutStatement.arg().accept(this);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        if(assignStatement.lv().equals(oldName)){
            var correspondingSymbol = assignStatement.getSymbolTable().varLookup(oldName);
            if(correspondingSymbol == symbol){
                assignStatement.setLv(newName);
            }
        }
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        if(assignArrayStatement.lv().equals(oldName)){
            var correspondingSymbol = assignArrayStatement.getSymbolTable().varLookup(oldName);
            if(correspondingSymbol == symbol){
                assignArrayStatement.setLv(newName);
            }
        }

        assignArrayStatement.index().accept(this);
        assignArrayStatement.rv().accept(this);
    }

    @Override
    public void visit(AndExpr e) {
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(LtExpr e) {
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(AddExpr e) {
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(SubtractExpr e) {
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(MultExpr e) {
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(ArrayAccessExpr e) {
       e.indexExpr().accept(this);
       e.arrayExpr().accept(this);
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);
    }

    @Override
    public void visit(MethodCallExpr e) {
        for(var actual : e.actuals()) actual.accept(this);
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
        if(e.id().equals(oldName)){
            var correspondingSymbol = e.getSymbolTable().varLookup(oldName);
            if(correspondingSymbol == symbol){
                e.setId(newName);
            }
        }
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
        e.e().accept(this);
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
        if(t.id().equals(oldName)){
            var correspondingSymbol = t.getSymbolTable().varLookup(oldName);
            if(correspondingSymbol == symbol){
                t.setId(newName);
            }
        }
    }
}
