package ex2;

import ast.*;
import ex1.ClassMapping;
import ex1.StaticClassVisitor;
import ex1.Symbol;
import ex1.SymbolTable;
import ex1.SymbolKind;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CompileVisitor implements Visitor {
	private final StringBuilder builder = new StringBuilder();
	private final ClassMapping classMapping;
	private Map<Symbol, Integer> symbolMapping = null; // maps symbol to its index, no need to offset fields
	private Map<String, ClassPointer> pointerMap = null;
	private int lastRegisterNumber = 0;
	private int lastLabelNumber = 0;

	public CompileVisitor(ClassMapping classMapping) {
		this.classMapping = classMapping;
	}

	public String getString() {
		return builder.toString();
	}
	
	public void helperFuncAppend() {
		builder.append("declare i8* @calloc(i32, i32)\n"
				+ "declare i32 @printf(i8*, ...)\n"
				+ "declare void @exit(i32)\n"
				+ "\n"
				+ "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n"
				+ "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n"
				+ "define void @print_int(i32 %i) {\n"
				+ "	%_str = bitcast [4 x i8]* @_cint to i8*\n"
				+ "	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n"
				+ "	ret void\n"
				+ "}\n"
				+ "\n"
				+ "define void @throw_oob() {\n"
				+ "	%_str = bitcast [15 x i8]* @_cOOB to i8*\n"
				+ "	call i32 (i8*, ...) @printf(i8* %_str)\n"
				+ "	call void @exit(i32 1)\n"
				+ "	ret void\n"
				+ "}\n");
		builder.append("define i32 @main() {\n"
				+ "	%_0 = call i8* @calloc(i32 8, i32 8)\n"
				+ "	%_1 = bitcast i8* %_0 to i8***\n"
				+ "	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.Simple_vtable, i32 0, i32 0\n"
				+ "	store i8** %_2, i8*** %_1\n"
				+ "	%_3 = bitcast i8* %_0 to i8***\n"
				+ "	%_4 = load i8**, i8*** %_3\n"
				+ "	%_5 = getelementptr i8*, i8** %_4, i32 0\n"
				+ "	%_6 = load i8*, i8** %_5\n"
				+ "	%_7 = bitcast i8* %_6 to i32 (i8*)*\n"
				+ "	%_8 = call i32 %_7(i8* %_0)\n"
				+ "	call void (i32) @print_int(i32 %_8)\n"
				+ "	ret i32 0\n"
				+ "}\n"); //delete
	}

	@Override
	public void visit(Program program) {
		CompileClassesVisitor ccv = new CompileClassesVisitor();
		program.accept(ccv);
		builder.append(ccv.getString());
		symbolMapping = ccv.getMapping();
		pointerMap = ccv.getPointerMap();
		helperFuncAppend(); //test
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
			String s = String.format("define %s @%s.%s(i8* %%this", TypeDecider.llvmType(methodDecl.returnType()),
					classDecl.name(), methodDecl.name());
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
		methodDecl.ret().accept(this);
		addLine(String.format("ret i32 %%_%d", lastRegisterNumber - 1));
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
		int ownerPtr = lastRegisterNumber - 1;
		List<Integer> actualsRegisters = new LinkedList<>();
		for (var actual : e.actuals()) { // generate code for actuals and save their registers
			actual.accept(this);
			actualsRegisters.add(lastRegisterNumber - 1);
		}

		int casted = lastRegisterNumber++; // bitcast vtable pointer
		String s = String.format("%%_%d = bitcast i8* %%_%d to i8***", casted, ownerPtr);
		addLine(s);
		int vtablePtr = lastRegisterNumber++; // load vtable ptr
		s = String.format("%%_%d = load i8**, i8*** %%_%d", vtablePtr, casted);
		addLine(s);
		int vtableEntry = lastRegisterNumber++; // get pointer to the function using index
		s = String.format("%%_%d = getelementptr i8*, i8** %%_%d, i32 %d", vtableEntry, vtablePtr,
				symbolMapping.get(funcSymbol));
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
		s = String.format("%%_%d = call %s %%_%d(i8* %%_%d%s)", resultPtr, returnType, castedFunc, ownerPtr,
				callArgs.toString());
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
		String s = String.format("%%_%d = call i8* @calloc(i32 1, i32 %d)", tmp0,
				pointerMap.get(e.classId()).getClassSize());
		addLine(s);

		// setting the vtable
		int tmp1 = lastRegisterNumber++;
		s = String.format("%%_%d = bitcast i8* %%_%d to i8***", tmp1, tmp0);
		addLine(s);
		int tmp2 = lastRegisterNumber++;
		int classFuncs = pointerMap.get(e.classId()).numberOfFuncs();
		s = String.format("%%_%d = getelementptr [%d x i8*], [%d x i8*]* @.%s_vtable, i32 0, i32 0", tmp2, classFuncs,
				classFuncs, e.classId());
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
		String type = TypeDecider.llvmType(formalArg.type());
		String s = String.format("%%%s = alloca %s", formalArg.name(), type);
		addLine(s);
		addLine(String.format("store %s %%.%s, %s* %%%s", type, formalArg.name(), type, formalArg.name()));
	}

	@Override
	public void visit(VarDecl varDecl) {
		String s = String.format("%%%s = alloca %s", varDecl.name(), TypeDecider.llvmType(varDecl.type()));
		addLine(s);
	}

	// IF and JUMPS
	@Override
	public void visit(BlockStatement blockStatement) {
		for (var s : blockStatement.statements()) {
			builder.append("\n");
			s.accept(this);
		}
	}

	@Override
	public void visit(IfStatement ifStatement) {
		ifStatement.cond().accept(this);
		addLine("br i1 %_" + (lastRegisterNumber - 1) + ", " + "label %label" + lastLabelNumber + ", label %label"
				+ (lastLabelNumber + 1));
		builder.append("label" + lastLabelNumber++ + ":\n");
		ifStatement.thencase().accept(this);
		addLine("br label %label" + (lastLabelNumber + 1) + "\n");
		builder.append("label" + lastLabelNumber++ + ":\n");
		ifStatement.elsecase().accept(this);
		addLine("br label %label" + lastLabelNumber + "\n");
		builder.append("label" + lastLabelNumber++ + ":\n");
	}

	@Override
	public void visit(WhileStatement whileStatement) {
		addLine("br label %loop" + lastLabelNumber++);
		addLine("loop" + (lastLabelNumber - 1) + ":"); // loop cond
		whileStatement.cond().accept(this);
		addLine("br i1 %_" + (lastRegisterNumber - 1) + ", label %loop" + lastLabelNumber++ + ", label %loop"
				+ lastLabelNumber++);
		addLine("loop" + (lastLabelNumber - 2) + ":"); // loop body
		whileStatement.body().accept(this);
		addLine("br label %loop" + (lastLabelNumber - 3));
		addLine("loop" + (lastLabelNumber - 1) + ":"); // out of loop
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
		String arg;
		sysoutStatement.arg().accept(this);
		arg = "%_" + (lastRegisterNumber - 1);
		addLine("call void (i32) @print_int(i32 " + arg + ")");
	}

	@Override
	public void visit(AssignStatement assignStatement) {
		String type = TypeDecider.llvmType(assignStatement.getSymbolTable().varLookup(assignStatement.lv()).getDecl()), rv;
		assignStatement.rv().accept(this);
		rv = "%_" + (lastRegisterNumber - 1);
		addLine("store " + type + " " + rv + ", " + type + "* " + "%" + assignStatement.lv());
	}

	public void getAndOperands(LinkedList<Expr> operands, AndExpr e) {
		if (!(e.e1() instanceof AndExpr)) {
			operands.add(e.e1());
		} else {
			getAndOperands(operands, (AndExpr) e.e1());
		}
		if (!(e.e2() instanceof AndExpr)) {
			operands.add(e.e2());
		} else {
			getAndOperands(operands, (AndExpr) e.e2());
		}
	}

	// Exp and trees
	@Override
	public void visit(AndExpr e) {
		LinkedList<Expr> operands = new LinkedList<Expr>();
		getAndOperands(operands, e);
		operands.get(0).accept(this);
		addLine("br label %andcond" + lastLabelNumber);
		for (int i = 1; i < operands.size(); i++) {
			builder.append("andcond" + lastLabelNumber++ + ":\n");
			addLine("br i1 %_" + (lastRegisterNumber - 1) + ", label %andcond" + lastLabelNumber++ + ", label %andcond"
					+ lastLabelNumber);
			builder.append("andcond" + (lastLabelNumber - 1) + ":\n");
			operands.get(i).accept(this);
			addLine("br label %andcond" + lastLabelNumber);
			builder.append("andcond" + lastLabelNumber++ + ":\n");
			addLine("%_" + lastRegisterNumber++ + " = phi i1 [0, %andcond" + (lastLabelNumber - 3) + "], [%_"
					+ (lastRegisterNumber - 2) + ", %andcond" + (lastLabelNumber - 2) + "]");
			addLine("br label %andcond" + lastLabelNumber);
		}
		builder.append("andcond" + lastLabelNumber + ":\n");
	}

	@Override
	public void visit(LtExpr e) {
		String lv, rv;
		e.e1().accept(this);
		lv = "%_" + (lastRegisterNumber - 1);
		e.e2().accept(this);
		rv = "%_" + (lastRegisterNumber - 1);
		addLine("%_" + lastRegisterNumber++ + " = icmp slt i32 " + lv + ", " + rv);
	}

	private void visitBinaryExpr(BinaryExpr e, String opt) {
		String e1, e2;
		e.e1().accept(this);
		e1 = "%_" + (lastRegisterNumber - 1);
		e.e2().accept(this);
		e2 = "%_" + (lastRegisterNumber - 1);
		addLine("%_" + lastRegisterNumber++ + " = " + opt + " i32 " + e1 + ", " + e2);
	}

	@Override
	public void visit(AddExpr e) {
		visitBinaryExpr(e, "add");
	}

	@Override
	public void visit(SubtractExpr e) {
		visitBinaryExpr(e, "sub");
	}

	@Override
	public void visit(MultExpr e) {
		visitBinaryExpr(e, "mul");
	}

	@Override
	public void visit(NotExpr e) {
		String eval;
		e.accept(this);
		eval = "%_" + (lastRegisterNumber - 1);
		addLine("%_" + lastRegisterNumber++ + " = sub i1 1, " + eval);
	}

	@Override
	public void visit(IntegerLiteralExpr e) {
		addLine("%_" + lastRegisterNumber++ + " = add i32 0, " + e.num());
	}

	@Override
	public void visit(TrueExpr e) {
		addLine("%_" + lastRegisterNumber++ + " = add i1 1, 0");
	}

	@Override
	public void visit(FalseExpr e) {
		addLine("%_" + lastRegisterNumber++ + " = add i1 0, 0");
	}

	@Override
	public void visit(IdentifierExpr e) {
		String type = TypeDecider.llvmType(e.getSymbolTable().varLookup(e.id()).getDecl());
		addLine("%_" + lastRegisterNumber++ + " = load " + type + ", " + type + "* %" + e.id());
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
}
