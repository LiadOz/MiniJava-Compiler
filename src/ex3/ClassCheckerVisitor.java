package ex3;

import ast.*;
import ex1.ClassMapping;
import ex1.StaticClassVisitor;
import ex1.Symbol;
import ex1.SymbolTable;

public class ClassCheckerVisitor implements Visitor {

    // Points 4-9, 11, 12, 24
    // 1, 2, 3 implemented in InitialCheckerVisitor
    // 4, 5, 24 implemented in SymbolTable

    private final StaticClassVisitor classFinder = new StaticClassVisitor();
    private final ClassMapping classMap;
    private String currClass;

    public ClassCheckerVisitor(ClassMapping classMap){
        this.classMap = classMap;
    }

    @Override
    public void visit(Program program) {
        program.mainClass().accept(this);
        for (ClassDecl classDecl : program.classDecls())
            classDecl.accept(this);
    }

    @Override
    public void visit(ClassDecl classDecl) {
        currClass = classDecl.name();
        for (MethodDecl methodDecl : classDecl.methoddecls())
            methodDecl.accept(this);
        for (var field : classDecl.fields())
            field.accept(this);
    }

    @Override
    public void visit(MainClass mainClass) { mainClass.mainStatement().accept(this); }

    @Override
    public void visit(MethodDecl methodDecl) {
        String methodName = methodDecl.name();
        Symbol currSymbol = methodDecl.getSymbolTable().methodLookup(methodName);
        try { // Point 6
            Symbol overridden = methodDecl.getSymbolTable().getParent().methodLookup(methodName);
            String[] oParams = overridden.getArgumentTypes();
            String[] cParams = currSymbol.getArgumentTypes();
            if (oParams.length != cParams.length)
                throw new SemanticException("method override - different number of arguments");
            for (int i = 0; i < oParams.length; i++) {
                if (!oParams[i].equals(cParams[i]))
                    throw new SemanticException("method override - different static arguments");
            }
            if (!classMap.isValidSubclass(overridden.getReturnType(), currSymbol.getReturnType()))
                throw new SemanticException("method override - bad return type");
        } catch (Exception e) {

        }

        methodDecl.accept(this);
        for (FormalArg formalArg : methodDecl.formals())
            formalArg.accept(this);
        for (VarDecl varDecl : methodDecl.vardecls())
            varDecl.accept(this);

        for (Statement statement: methodDecl.body())
            statement.accept(this);
        methodDecl.ret().accept(this);
    }

    @Override
    public void visit(FormalArg formalArg) {
        formalArg.type().accept(this);
    }

    @Override
    public void visit(VarDecl varDecl) {
        varDecl.accept(this);
    }

    @Override
    public void visit(BlockStatement blockStatement) {
        for (var statement : blockStatement.statements())
            statement.accept(this);
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
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        assignArrayStatement.rv().accept(this);
        assignArrayStatement.index().accept(this);
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
        e.arrayExpr().accept(this);
        e.indexExpr().accept(this);
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);
    }

    @Override
    public void visit(MethodCallExpr e) { // Points 11 and 12
        e.ownerExpr().accept(this);
        classFinder.clearResult();
        e.ownerExpr().accept(classFinder); // this should be ref type else it is checked in TypeChecker i.e point 10
        var ownerClass = classFinder.getResult();
        if (ownerClass == null)
            throw new SemanticException("method call - invalid owner expression");

        SymbolTable st;
        if (ownerClass.equals(StaticClassVisitor.THIS_STRING))
            st = e.getSymbolTable();
        else
            st = classMap.get(ownerClass);

        var method = st.methodLookup(e.methodId());

        String[] origArgs = method.getArgumentTypes();
        if (origArgs.length != e.actuals().size())
            throw new SemanticException("method call - invalid number of arguments");

        int i = 0;
        for (var expr : e.actuals()) { // there is no need to accept each actual
            expr.accept(this);
            classFinder.clearResult();
            expr.accept(classFinder);
            String className = classFinder.getResult();
            if (className == null) {
                System.out.printf("class name = %s%n", e.methodId());
                throw new SemanticException("method call - invalid argument expression");
            }
            if (className.equals(StaticClassVisitor.THIS_STRING)) {
                className = currClass;
            }
            if (!classMap.isValidSubclass(origArgs[i], className)) {
                System.out.printf("i = %d, orig = %s, got = %s%n", i, origArgs[i], className);
                throw new SemanticException("method call - invalid argument class");
            }
            i++;
        }
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
        e.lengthExpr().accept(this);
    }

    @Override
    public void visit(NewObjectExpr e) {
        classMap.get(e.classId()); // Point 9
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
        classMap.get(t.id()); // Point 8
    }
}
