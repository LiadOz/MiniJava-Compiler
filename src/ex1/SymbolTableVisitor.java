package ex1;

import ast.*;
import ex2.TypeDecider;

/*
    This visitor inserts data to a SymbolTable root and ClassMapping
 */
// TODO: add valid method decl symbol
// TODO: figure out if varType and currSymbolType are valid ways to save data
public class SymbolTableVisitor implements Visitor {
    private final SymbolTable root;
    private final ClassMapping classMapping;
    private String varType;
    private SymbolKind currSymbolType;
    private SymbolTable currTable;

    public SymbolTableVisitor(SymbolTable root, ClassMapping classMapping) {
        this.classMapping = classMapping;
        this.root = root;
    }

    @Override
    public void visit(Program program) {
        for (ClassDecl classDecl : program.classDecls()) {
            classDecl.accept(this);
        }
        program.setSymbolTable(root);
    }

    @Override
    public void visit(ClassDecl classDecl) {
        SymbolTable parent = root;
        if (classDecl.superName() != null) {
            parent = classMapping.get(classDecl.superName());
            if (parent == null) {
                throw new RuntimeException("invalid superclass");
            }
        }
        currTable = new SymbolTable(parent);
        classDecl.setSymbolTable(currTable);
        classMapping.add(classDecl.name(), currTable);

        for (VarDecl varDecl : classDecl.fields()) {
            currSymbolType = SymbolKind.FIELD;
            varDecl.accept(this);
        }
        for (MethodDecl methodDecl : classDecl.methoddecls()) {
            currSymbolType = SymbolKind.VAR;
            methodDecl.accept(this);
        }
    }

    @Override
    public void visit(MainClass mainClass) {
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        StringBuilder decl = new StringBuilder();
        for (FormalArg formalArg : methodDecl.formals()) {
            if (!decl.toString().equals(""))
                decl.append(Symbol.DECL_SEP);
            decl.append(TypeDecider.javaType(formalArg.type()));
        }
        System.out.println("problem here");
        decl.append(Symbol.DECL_MAJOR_SEP).append(TypeDecider.javaType(methodDecl.returnType()));

        Symbol method = new Symbol(
                methodDecl.name(), SymbolKind.METHOD, decl.toString(), methodDecl);
        // TODO: figure out method decl
        currTable.addMethod(methodDecl.name(), method);
        SymbolTable prev = currTable;
        currTable = new SymbolTable(prev);
        methodDecl.setSymbolTable(currTable);
        for (FormalArg formalArg : methodDecl.formals())
            formalArg.accept(this);
        for (VarDecl varDecl : methodDecl.vardecls())
            varDecl.accept(this);
        for (Statement statement : methodDecl.body())
            statement.accept(this);
        methodDecl.ret().accept(this);

        currTable = prev;
    }

    @Override
    public void visit(FormalArg formalArg) {
        formalArg.type().accept(this); // use this to get varType
        Symbol s = new Symbol(
                formalArg.name(), currSymbolType, varType, formalArg);
        currTable.addVar(formalArg.name(), s);
        formalArg.setSymbolTable(currTable);
    }

    @Override
    public void visit(VarDecl varDecl) {
        varDecl.type().accept(this);
        Symbol s = new Symbol(
                varDecl.name(), currSymbolType, varType, varDecl);
        currTable.addVar(varDecl.name(), s);
        varDecl.setSymbolTable(currTable);
    }

    @Override
    public void visit(BlockStatement blockStatement) {
        blockStatement.setSymbolTable(currTable);
        for (Statement statement : blockStatement.statements())
            statement.accept(this);
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.setSymbolTable(currTable);
        ifStatement.cond().accept(this);
        ifStatement.thencase().accept(this);
        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.setSymbolTable(currTable);
        whileStatement.body().accept(this);
        whileStatement.cond().accept(this);
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.setSymbolTable(currTable);
        sysoutStatement.arg().accept(this);
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        assignStatement.setSymbolTable(currTable);
        assignStatement.rv().accept(this);
    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
        assignArrayStatement.setSymbolTable(currTable);
        assignArrayStatement.rv().accept(this);
        assignArrayStatement.index().accept(this);
    }

    @Override
    public void visit(AndExpr e) {
        e.setSymbolTable(currTable);
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(LtExpr e) {
        e.setSymbolTable(currTable);
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(AddExpr e) {
        e.setSymbolTable(currTable);
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(SubtractExpr e) {
        e.setSymbolTable(currTable);
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(MultExpr e) {
        e.setSymbolTable(currTable);
        e.e1().accept(this);
        e.e2().accept(this);
    }

    @Override
    public void visit(ArrayAccessExpr e) {
        e.setSymbolTable(currTable);
        e.arrayExpr().accept(this);
        e.indexExpr().accept(this);
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.setSymbolTable(currTable);
        e.arrayExpr().accept(this);

    }

    @Override
    public void visit(MethodCallExpr e) {
        e.setSymbolTable(currTable);
        for (var a : e.actuals()) {
            a.accept(this);
        }
        e.ownerExpr().accept(this);
    }

    @Override
    public void visit(IntegerLiteralExpr e) {
        e.setSymbolTable(currTable);
    }

    @Override
    public void visit(TrueExpr e) {
        e.setSymbolTable(currTable);
    }

    @Override
    public void visit(FalseExpr e) {
        e.setSymbolTable(currTable);
    }

    @Override
    public void visit(IdentifierExpr e) {
        e.setSymbolTable(currTable);
    }

    @Override
    public void visit(ThisExpr e) {
        e.setSymbolTable(currTable);
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.setSymbolTable(currTable);
        e.lengthExpr().accept(this);
    }

    @Override
    public void visit(NewObjectExpr e) {
        e.setSymbolTable(currTable);
    }

    @Override
    public void visit(NotExpr e) {
        e.setSymbolTable(currTable);
        e.e().accept(this);
    }

    @Override
    public void visit(IntAstType t) {
        t.setSymbolTable(currTable);
        varType = "int";
    }

    @Override
    public void visit(BoolAstType t) {
        t.setSymbolTable(currTable);
        varType = "boolean";
    }

    @Override
    public void visit(IntArrayAstType t) {
        t.setSymbolTable(currTable);
        varType = "int[]";
    }

    @Override
    public void visit(RefType t) {
        t.setSymbolTable(currTable);
        varType = t.id();
    }
}
