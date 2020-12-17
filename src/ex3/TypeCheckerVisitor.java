package ex3;

import ast.*;
import ex1.ClassMapping;

import java.util.HashMap;

public class TypeCheckerVisitor implements Visitor {

    // Points 10, 13, 14, 16-23

    private final ClassMapping classMap;
    private final HashMap<AstNode, String> nodeTypes = new HashMap<>();

    private void addNodeType(AstNode node, String type){
        nodeTypes.put(node, type);
    }

    private String getNodeType(AstNode node){
        return nodeTypes.get(node);
    }

    public TypeCheckerVisitor(ClassMapping classMap){
        this.classMap = classMap;
    }
    @Override
    public void visit(Program program) {
        program.mainClass().accept(this);
        for(var classDecl : program.classDecls()){
            classDecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for(var methodDecl : classDecl.methoddecls()){
            methodDecl.accept(this);
        }

    }

    @Override
    public void visit(MainClass mainClass) {
        mainClass.mainStatement().accept(this);
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        for(var statement: methodDecl.body()){
            statement.accept(this);
        }
        var methodReturnType = methodDecl.getSymbolTable().methodLookup(methodDecl.name()).getReturnType();
        methodDecl.ret().accept(this);
        var actualReturnType = getNodeType(methodDecl.ret());
        if(!classMap.isValidSubclass(methodReturnType,actualReturnType)){
            throw new SemanticException(String.format("Error in method %s: expected return type %s but got %s", methodDecl.name(), methodReturnType, actualReturnType));
        }

    }

    @Override
    public void visit(FormalArg formalArg) {

    }

    @Override
    public void visit(VarDecl varDecl) {

    }

    @Override
    public void visit(BlockStatement blockStatement) {

    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.cond().accept(this);
        if(!getNodeType(ifStatement.cond()).equals("boolean")){
            throw new SemanticException("Error: condition is not a boolean in if statement");
        }
        ifStatement.thencase().accept(this);
        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);
        if(!getNodeType(whileStatement.cond()).equals("boolean")){
            throw new SemanticException("Error: condition is not a boolean in while statement");
        }
        whileStatement.body().accept(this);
    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
        sysoutStatement.arg().accept(this);
        var argType = getNodeType(sysoutStatement.arg());
        if(!argType.equals("int")){
            throw new SemanticException("Error: print statement expected int but got " + argType); // Point 20
        }
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        var lvType = assignStatement.getSymbolTable().varLookup(assignStatement.lv()).getDecl();
        assignStatement.rv().accept(this);
        var rvType = getNodeType(assignStatement.rv());
        if(!classMap.isValidSubclass(lvType, rvType)){
            throw new SemanticException(String.format("Error: tried to assign %s to a %s variable", rvType, lvType)); // Point 16
        }
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
