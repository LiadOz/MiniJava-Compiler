package ex2;

import ast.*;
import ex1.ClassMapping;
import ex1.StaticClassVisitor;
import ex1.Symbol;
import ex1.SymbolTable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompileVisitor implements Visitor {
    private final StringBuilder builder = new StringBuilder();
    private final ClassMapping classMapping;
    private Map<Symbol, Integer> symbolMapping = null; // maps symbol to its index, no need to offset fields
    private Map<String, ClassPointer> pointerMap = null;
    private int lastRegisterNumber = 0;

    public CompileVisitor(ClassMapping classMapping) {
        this.classMapping = classMapping;
    }

    public String getString() {
        return builder.toString();
    }

    @Override
    public void visit(Program program) {
        CompileClassesVisitor ccv = new CompileClassesVisitor();
        program.accept(ccv);
        builder.append(ccv.getString());
        symbolMapping = ccv.getMapping();
        pointerMap = ccv.getPointerMap();

        program.mainClass().accept(this);
        for (var classDecl : program.classDecls())
            classDecl.accept(this);
    }


    @Override
    public void visit(MainClass mainClass) {
    }

    // OOP

    private void addLine(String line) {
        builder.append("\t").append(line).append("\n");
    }

    @Override
    public void visit(ClassDecl classDecl) {
        for (var methodDecl : classDecl.methoddecls()) {
            String s = String.format("define %s @%s.%s(i8* %%this",
                    TypeDecider.llvmType(methodDecl.returnType()), classDecl.name(), methodDecl.name());
            builder.append(s);
            methodDecl.accept(this);
        }
    }

    @Override
    public void visit(MethodDecl methodDecl) { // this compiles from the formals after %this
        for (var formal : methodDecl.formals()) {
            String s = String.format(", %s %%.%s", TypeDecider.llvmType(formal.type()), formal.name());
            builder.append(s);
        }
        builder.append(") {\n");
        lastRegisterNumber = 0; // it's that easy
        for (var formal : methodDecl.formals())
            formal.accept(this);
        for (var varDecl : methodDecl.vardecls())
            varDecl.accept(this);
        for (var statement : methodDecl.body())
            statement.accept(this);
        builder.append("}\n\n");
    }

    @Override
    public void visit(MethodCallExpr e) {
        // first determine what function is called
        StaticClassVisitor classFinder = new StaticClassVisitor();
        e.ownerExpr().accept(classFinder);
        String ownerClass = classFinder.getResult();
        SymbolTable st;
        if (ownerClass.equals("current"))
            st = e.getSymbolTable();
        else
            st = classMapping.get(ownerClass);
        Symbol funcSymbol = st.methodLookup(e.methodId());

        e.ownerExpr().accept(this); // this should generate the owner ptr
        int ownerPtr = lastRegisterNumber;
        List<Integer> actualsRegisters = new LinkedList<>();
        for (var actual : e.actuals()) { // generate code for actuals and save their registers
            actual.accept(this);
            actualsRegisters.add(lastRegisterNumber);
        }

        int casted = lastRegisterNumber++; // bitcast vtable pointer
        String s = String.format("%%_%d = bitcast i8* %%_%d to i8***", casted, ownerPtr);
        addLine(s);
        int vtablePtr = lastRegisterNumber++; // load vtable ptr
        s = String.format("%%_%d = load i8**, i8*** %%_%d", vtablePtr, casted);
        addLine(s);
        int vtableEntry = lastRegisterNumber++; // get pointer to the function using index
        s = String.format("%%_%d = getelementptr i8*, i8** %%_%d, i32 %d",
                vtableEntry, vtablePtr, symbolMapping.get(funcSymbol));
        addLine(s);
        int funcPtr = lastRegisterNumber++; // read the actual function pointer
        s = String.format("%%_%s = load i8*, i8** %%_%s", funcPtr, vtableEntry);
        addLine(s);

        int castedFunc = lastRegisterNumber++; // cast into matching signature
        StringBuilder sig = new StringBuilder(); // signature
        StringBuilder callArgs = new StringBuilder(); // call arguments for next command
        String returnType = TypeDecider.llvmType(funcSymbol.getDecl().split(Symbol.DECL_MAJOR_SEP)[1]);
        sig.append(returnType);
        sig.append(" (i8*");
        String[] splitted = funcSymbol.getDecl().split(Symbol.DECL_MAJOR_SEP)[0].split(Symbol.DECL_SEP);
        for (int i = 0; i < splitted.length; i++) {
            sig.append(", ");
            callArgs.append(", ");
            sig.append(TypeDecider.llvmType(splitted[i]));
            callArgs.append(TypeDecider.llvmType(splitted[i]));
            callArgs.append(" %_").append(actualsRegisters.indexOf(i));
        }
        sig.append(")*");
        s = String.format("%%_%d = bitcast i8* %%_%d to %s", castedFunc, funcPtr, sig.toString());
        addLine(s);

        int resultPtr = lastRegisterNumber++; // calling the function with args that were created at the top
        s = String.format("%%_%d = call %s %%_%d(i8* %%_%d%s)",
                resultPtr, returnType, castedFunc, ownerPtr, callArgs.toString());
        addLine(s);
    }

    @Override
    public void visit(ThisExpr e) {
        String s = String.format("%%_%d = add i8* 0, %%this", lastRegisterNumber++);
        addLine(s);
    }

    @Override
    public void visit(NewObjectExpr e) {
        // allocation
        int tmp0 = lastRegisterNumber++;
        String s = String.format("%%_%d = call i8* @calloc(i32 1, i32 %d)",
                tmp0, pointerMap.get(e.classId()).getClassSize());
        addLine(s);

        // setting the vtable
        int tmp1 = lastRegisterNumber++;
        s = String.format("%%_%d = bitcast i8* %%_%d to i8***", tmp1, tmp0);
        addLine(s);
        int tmp2 = lastRegisterNumber++;
        int classFuncs = pointerMap.get(e.classId()).numberOfFuncs();
        s = String.format("%%_%d = getelementptr [%d x i8*], [%d x i8*]* @.%s_vtable, i32 0, i32 0",
                tmp2, classFuncs, classFuncs, e.classId());
        addLine(s);
        s = String.format("store i8** %%_%d, i8*** %%_%d", tmp2, tmp1);
        addLine(s);
        // creating the result which can be saved
        int resReg = lastRegisterNumber++;
        s = String.format("%%_%d = add i8* 0, %d", resReg, tmp0);
        addLine(s);
    }

    @Override
    public void visit(FormalArg formalArg) {
        String s = String.format("%%%s = alloca %s", formalArg.name(), TypeDecider.llvmType(formalArg.type()));
        addLine(s);
    }

    @Override
    public void visit(VarDecl varDecl) {
        String s = String.format("%%%s = alloca %s", varDecl.name(), TypeDecider.llvmType(varDecl.type()));
        addLine(s);
    }

    // IF and JUMPS
    @Override
    public void visit(BlockStatement blockStatement) {

    }

    @Override
    public void visit(IfStatement ifStatement) {

    }

    @Override
    public void visit(WhileStatement whileStatement) {

    }
    // ARRAYS
    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {
    }

    @Override
    public void visit(ArrayAccessExpr e) {
    }

    @Override
    public void visit(ArrayLengthExpr e) {

    }

    @Override
    public void visit(NewIntArrayExpr e) {

    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {
    }

    @Override
    public void visit(AssignStatement assignStatement) {
        // define temp assign from rv
    }

    // Exp and trees
    @Override
    public void visit(AndExpr e) {
        // implement short circuit
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
    public void visit(NotExpr e) {

    }

    // SIMPLE
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
