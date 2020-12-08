package ex2;

import ast.*;
import ex1.Symbol;
import ex1.SymbolTable;

import java.util.HashMap;
import java.util.Map;

// this class builds the initial vtables and sets a mapping between symbols and where they are stored in objects
public class CompileClassesVisitor implements Visitor {
    private final StringBuilder builder = new StringBuilder();
    private final Map<String, ClassPointer> pointerMap = new HashMap<>();
    private final Map<Symbol, Integer> symbolMapping = new HashMap<>(); // maps symbol to its index

    public String getString() {
        return builder.toString();
    }

    public Map<Symbol, Integer> getMapping() {
        return symbolMapping;
    }

    public Map<String, ClassPointer> getPointerMap() {
        return pointerMap;
    }

    @Override
    public void visit(Program program) {
        for (var classDecl : program.classDecls())
            classDecl.accept(this);
    }

    @Override
    public void visit(ClassDecl classDecl) {
        ClassPointer cp;
        if (classDecl.superName() == null) {
            cp = new ClassPointer(classDecl.name());
        }
        else {
            cp = pointerMap.get(classDecl.superName()).subclassPointer(classDecl.name());
        }
        pointerMap.put(classDecl.name(), cp);

        mapFunctions(cp, classDecl);
        mapFields(cp, classDecl);

        compileVtable(classDecl.name(), cp);
    }

    private void compileVtable(String className, ClassPointer cp) {
        builder.append("@.");
        builder.append(className);
        builder.append("_vtable = global [");
        var entries = cp.getSortedMethods();
        builder.append(entries.size());
        builder.append(" x i8*] [");

        for (var vte: entries) {
            builder.append("\n");
            vte.getMethodDecl().accept(this); // takes care up until @
            builder.append("@");
            builder.append(vte.getClassName());
            builder.append(".");
            builder.append(vte.getFuncName());
            builder.append(" to i8*)");
            builder.append(",");
        }
        if (entries.size() > 0)
            builder.deleteCharAt(builder.length() - 1);
        builder.append("\n]\n");
    }

    private void mapFunctions(ClassPointer cp, ClassDecl classDecl) {
        SymbolTable classTable = classDecl.getSymbolTable();
        for (var methodDecl : classDecl.methoddecls()) {
            Symbol s = classTable.methodLookup(methodDecl.name());
            int index = cp.addFunction(methodDecl.name(), methodDecl);
            symbolMapping.put(s, index);
        }
    }

    private void mapFields(ClassPointer cp, ClassDecl classDecl) {
        SymbolTable classTable = classDecl.getSymbolTable();
        for (var varDecl : classDecl.fields()) {
            Symbol s = classTable.varLookup(varDecl.name());
            int index = cp.addField(varDecl.name(), TypeDecider.llvmSize(varDecl.type()));
            symbolMapping.put(s, index);
        }
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        builder.append(String.format("\t i8* bitcast (%s (i8*, ", TypeDecider.llvmType(methodDecl.returnType())));

        for (var formalArg : methodDecl.formals()) {
            formalArg.accept(this);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(")* ");
    }

    @Override
    public void visit(FormalArg formalArg) {
        formalArg.type().accept(this);
    }

    @Override
    public void visit(IntAstType t) {
        builder.append("i32");
    }

    @Override
    public void visit(BoolAstType t) {
        builder.append("i1");
    }

    @Override
    public void visit(IntArrayAstType t) {
        builder.append("i32*");
    }

    @Override
    public void visit(RefType t) {
        builder.append("i8*");
    }

    @Override
    public void visit(MainClass mainClass) {

    }

    @Override
    public void visit(VarDecl varDecl) {

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

}
