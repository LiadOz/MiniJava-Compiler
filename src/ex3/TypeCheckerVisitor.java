package ex3;

import ast.*;
import ex1.ClassMapping;

import java.util.HashMap;

public class TypeCheckerVisitor implements Visitor {

    // Points 10, 13, 14, 16-23

    private final ClassMapping classMap;
    private final HashMap<AstNode, String> nodeTypes = new HashMap<>();
    private String currentClass;

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
        currentClass = program.mainClass().name();
        program.mainClass().accept(this);
        for(var classDecl : program.classDecls()){
            currentClass = classDecl.name();
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
        if(!classMap.isValidSubclass(methodReturnType, actualReturnType)){
            throw new SemanticException(String.format("Error in method %s: expected return type %s but got %s", methodDecl.name(), methodReturnType, actualReturnType)); // Point 18
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
        for( var statement: blockStatement.statements()){
            statement.accept(this);
        }
    }

    @Override
    public void visit(IfStatement ifStatement) {
        ifStatement.cond().accept(this);
        var condType = getNodeType(ifStatement.cond());
        if(!condType.equals("boolean")){
            throw new SemanticException("Error: If statement condition expected boolean but got " + condType ); // Point 17
        }
        ifStatement.thencase().accept(this);
        ifStatement.elsecase().accept(this);
    }

    @Override
    public void visit(WhileStatement whileStatement) {
        whileStatement.cond().accept(this);
        if(!getNodeType(whileStatement.cond()).equals("boolean")){
            throw new SemanticException("Error: condition is not a boolean in while statement"); // Point 17
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
        //Point 23
        var lvType = assignArrayStatement.getSymbolTable().varLookup(assignArrayStatement.lv()).getDecl();
        if(!lvType.equals("int[]")){
            throw new SemanticException("Error: Tried to make array access on a " + lvType);
        }

        assignArrayStatement.index().accept(this);
        var indexType = getNodeType(assignArrayStatement.index());
        if(!indexType.equals("int")){
            throw new SemanticException("Error: Array index expecting int but got " + indexType);
        }

        assignArrayStatement.rv().accept(this);
        var rvType = getNodeType(assignArrayStatement.rv());
        if(!rvType.equals("int")){
            throw new SemanticException("Error: Tried to assign " + rvType + " into an array");
        }

    }

    @Override
    public void visit(AndExpr e) {
        e.e1().accept(this);
        var e1Type = getNodeType(e.e1());
        if(!e1Type.equals("boolean")){
            throw new SemanticException("Error: And operator expected boolean but got " + e1Type);
        }

        e.e2().accept(this);
        var e2Type = getNodeType(e.e2());
        if(!e2Type.equals("boolean")){
            throw new SemanticException("Error: And operator expected boolean but got " + e2Type);
        }

        addNodeType(e, "boolean");

    }

    @Override
    public void visit(LtExpr e) {
        e.e1().accept(this);
        var e1Type = getNodeType(e.e1());
        if(!e1Type.equals("int")){
            throw new SemanticException("Error: LT operator expected int but got " + e1Type);
        }

        e.e2().accept(this);
        var e2Type = getNodeType(e.e2());
        if(!e2Type.equals("int")){
            throw new SemanticException("Error: LT operator expected int but got " + e2Type);
        }

        addNodeType(e, "boolean");

    }

    @Override
    public void visit(AddExpr e) {
        e.e1().accept(this);
        var e1Type = getNodeType(e.e1());
        if(!e1Type.equals("int")){
            throw new SemanticException("Error: Add operator expected int but got " + e1Type);
        }

        e.e2().accept(this);
        var e2Type = getNodeType(e.e2());
        if(!e2Type.equals("int")){
            throw new SemanticException("Error: Add operator expected int but got " + e2Type);
        }

        addNodeType(e, "int");
    }

    @Override
    public void visit(SubtractExpr e) {
        e.e1().accept(this);
        var e1Type = getNodeType(e.e1());
        if(!e1Type.equals("int")){
            throw new SemanticException("Error: Sub operator expected int but got " + e1Type);
        }

        e.e2().accept(this);
        var e2Type = getNodeType(e.e2());
        if(!e2Type.equals("int")){
            throw new SemanticException("Error: Sub operator expected int but got " + e2Type);
        }

        addNodeType(e, "int");
    }

    @Override
    public void visit(MultExpr e) {

        e.e1().accept(this);
        var e1Type = getNodeType(e.e1());
        if(!e1Type.equals("int")){
            throw new SemanticException("Error: Mult operator expected int but got " + e1Type);
        }

        e.e2().accept(this);
        var e2Type = getNodeType(e.e2());
        if(!e2Type.equals("int")){
            throw new SemanticException("Error: Mult operator expected int but got " + e2Type);
        }


        addNodeType(e, "int");

    }

    @Override
    public void visit(ArrayAccessExpr e) {
        //Point 22
        e.arrayExpr().accept(this);
        var arrayExprType = getNodeType(e.arrayExpr());
        if(!arrayExprType.equals("int[]")){
            throw new SemanticException("Error: Array access expected int[] but got " + arrayExprType);
        }

        e.indexExpr().accept(this);
        var indexExprType = getNodeType(e.indexExpr());
        if(!indexExprType.equals("int")){
            throw new SemanticException("Error: Array access index expected int but got " + indexExprType);
        }

        addNodeType(e, "int");
    }

    @Override
    public void visit(ArrayLengthExpr e) {
        e.arrayExpr().accept(this);
        var arrayExprType = getNodeType(e.arrayExpr());
        if(!arrayExprType.equals("int[]")){
            throw new SemanticException("Error: Array length expected int[] but got " + arrayExprType); // Point 13
        }

        addNodeType(e, "int");
    }

    @Override
    public void visit(MethodCallExpr e) {
        e.ownerExpr().accept(this);
        var ownerType = getNodeType(e.ownerExpr());

        if(classMap.get(ownerType) == null){ // Point 10
            throw new SemanticException("Error: Tried to invoke a function on a simple type - " + ownerType);
        }

        var methodSymbol = classMap.get(ownerType).methodLookup(e.methodId());

        var argTypes = methodSymbol.getArgumentTypes();

        var args = e.actuals();

        if(argTypes.length != args.size()){
            throw new SemanticException(String.format("Error: The method %s expected %s arguments but got %s", e.methodId(), argTypes.length, args.size()));
        }

        for(int i = 0 ; i < args.size(); i++){
            var arg = args.get(i);
            arg.accept(this);
            var argType = getNodeType(arg);
            if(!classMap.isValidSubclass(argTypes[i], argType)){
                throw new SemanticException(String.format("Error: Method %s expected %s at position %s but got %s", e.methodId(), argTypes[i], i+1, argType));
            }
        }

        addNodeType(e, methodSymbol.getReturnType());
    }

    @Override
    public void visit(IntegerLiteralExpr e) {
        addNodeType(e, "int");
    }

    @Override
    public void visit(TrueExpr e) {
        addNodeType(e, "boolean");
    }

    @Override
    public void visit(FalseExpr e) {
        addNodeType(e, "boolean");
    }

    @Override
    public void visit(IdentifierExpr e) {
        var idType = e.getSymbolTable().varLookup(e.id()).getDecl();
        addNodeType(e,idType);
    }

    @Override
    public void visit(ThisExpr e) {
        addNodeType(e, currentClass);
    }

    @Override
    public void visit(NewIntArrayExpr e) {
        e.lengthExpr().accept(this);
        var lengthType = getNodeType(e.lengthExpr());
        if(!lengthType.equals("int")){
            throw new SemanticException("Error: New array length expected int but got " + lengthType);
        }

        addNodeType(e, "int[]");

    }

    @Override
    public void visit(NewObjectExpr e) {
        if(classMap.get(e.classId()) == null){
            throw new SemanticException("Error: Tried to initialize a new object with an invalid class " + e.classId());
        }

        addNodeType(e, e.classId());
    }

    @Override
    public void visit(NotExpr e) {
        e.e().accept(this);
        var exprType = getNodeType(e.e());
        if(!exprType.equals("boolean")){
            throw new SemanticException("Tried to apply the not operator on a non-boolean");
        }

        addNodeType(e, "boolean");
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
