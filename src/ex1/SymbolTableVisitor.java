package ex1;

import ast.*;

import java.util.HashMap;
import java.util.Map;

/*
    This visitor inserts data to a SymbolTable root
 */
// TODO: add valid method decl symbol
// TODO: figure out if varType and currSymbolType are valid ways to save data
public class SymbolTableVisitor implements Visitor {
    private SymbolTable root;
    private String varType;
    private SymbolKind currSymbolType;
    private SymbolTable currTable;
    private Map<String, SymbolTable> classMap;

    public SymbolTableVisitor(SymbolTable root) {
        classMap = new HashMap<String, SymbolTable>();
        this.root = root;
    }

    @Override
    public void visit(Program program) {
        for (ClassDecl classDecl : program.classDecls()){
            classDecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        SymbolTable parent = root;
        if (classDecl.superName() != null){
            parent = classMap.get(classDecl.superName());
            if (parent == null){
                throw new RuntimeException("invalid superclass");
            }
        }
        currTable = new SymbolTable(parent);
        classMap.put(classDecl.name(), currTable);
        for (VarDecl varDecl : classDecl.fields()){
            currSymbolType = SymbolKind.FIELD;
            varDecl.accept(this);
        }
        for (MethodDecl methodDecl : classDecl.methoddecls()){
            currSymbolType = SymbolKind.VAR;
            methodDecl.accept(this);
        }
    }

    @Override
    public void visit(MainClass mainClass) { }

    @Override
    public void visit(MethodDecl methodDecl) {
        Symbol method = new Symbol(
                methodDecl.name(), SymbolKind.METHOD, "", methodDecl);
        currTable.addMethod(methodDecl.name(), method);
        SymbolTable prev = currTable;
        currTable = new SymbolTable(prev);
        for (FormalArg varDecl : methodDecl.formals())
            varDecl.accept(this);
        for (VarDecl varDecl : methodDecl.vardecls())
            varDecl.accept(this);

        currTable = prev;
    }

    @Override
    public void visit(FormalArg formalArg) {
        formalArg.type().accept(this); // use this to get varType
        Symbol s = new Symbol(
                formalArg.name(), currSymbolType, varType, formalArg);
        currTable.addVar(formalArg.name(), s);
    }

    @Override
    public void visit(VarDecl varDecl) {
        varDecl.type().accept(this);
        Symbol s = new Symbol(
                varDecl.name(), currSymbolType, varType, varDecl);
        currTable.addVar(varDecl.name(), s);
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
        varType = "int";
    }

    @Override
    public void visit(BoolAstType t) {
        varType = "bool";
    }

    @Override
    public void visit(IntArrayAstType t) {
        varType = "int[]";
    }

    @Override
    public void visit(RefType t) {
        varType = t.id();
    }
}
