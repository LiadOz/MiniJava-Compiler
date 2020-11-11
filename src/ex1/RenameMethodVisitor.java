package ex1;

import ast.*;

public class RenameMethodVisitor implements Visitor {

    private final Symbol symbol;
    private final String newName;
    private final ClassMapping classMapping;
    public RenameMethodVisitor(Symbol symbol, ClassMapping classMapping, String newName) {
        this.symbol = symbol;
        this.classMapping = classMapping;
        this.newName = newName;
    }

    @Override
    public void visit(Program program) {
        program.mainClass().accept(this);
        for (ClassDecl classDecl : program.classDecls()){
            classDecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for (MethodDecl methodDecl: classDecl.methoddecls())
            methodDecl.accept(this);
    }

    @Override
    public void visit(MainClass mainClass) {
        mainClass.mainStatement().accept(this);
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        if (symbol == methodDecl.getSymbolTable().parentMethodLookup(methodDecl.name())){
            methodDecl.getSymbolTable().methodLookup(methodDecl.name()).setId(newName);
            methodDecl.setName(newName);
        }
        for (Statement statement: methodDecl.body())
            statement.accept(this);
        methodDecl.ret().accept(this);
    }

    @Override
    public void visit(FormalArg formalArg) { }

    @Override
    public void visit(VarDecl varDecl) { }

    @Override
    public void visit(BlockStatement blockStatement) {
        for (var statement : blockStatement.statements())
            statement.accept(this);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.thencase().accept(this);
        ifStatement.elsecase().accept(this);
        ifStatement.cond().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.body().accept(this);
        whileStatement.cond().accept(this);
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
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
        StaticClassVisitor classFinder = new StaticClassVisitor();
        e.ownerExpr().accept(classFinder);
        String ownerClass = classFinder.getResult();

        SymbolTable st;
        if (ownerClass.equals("current"))
            st = e.getSymbolTable();
        else
            st = classMapping.get(ownerClass);

        if (symbol == st.parentMethodLookup(e.methodId()))
            e.setMethodId(newName);
    }

    @Override
    public void visit(IntegerLiteralExpr e) { }

    @Override
    public void visit(TrueExpr e) { }

    @Override
    public void visit(FalseExpr e) { }

    @Override
    public void visit(IdentifierExpr e) { }

    @Override
    public void visit(ThisExpr e) { }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.lengthExpr().accept(this);
    }

    @Override
    public void visit(NewObjectExpr e) { }

    @Override
    public void visit(NotExpr e) {
        e.e().accept(this);
    }

    @Override
    public void visit(IntAstType t) { }

    @Override
    public void visit(BoolAstType t) { }

    @Override
    public void visit(IntArrayAstType t) { }

    @Override
    public void visit(RefType t) { }
}
