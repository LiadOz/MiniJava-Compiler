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
				+ "}\n\n");
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
	    builder.append("define i32 @main() {\n");
	    mainClass.mainStatement().accept(this);
	    addLine("ret i32 0");
	    builder.append("}\n\n");
	}

	// OOP

	private void addLine(String line) {
		builder.append("\t").append(line).append("\n");
	}

	private void addLabel(String label){
		builder.append(label).append("\n");
	}

	@Override
	public void visit(ClassDecl classDecl) {
		for (var methodDecl : classDecl.methoddecls()) {
			String s = String.format("define %s @%s.%s(i8* %%this", TypeDecider.llvmType(methodDecl.returnType()), classDecl.name(), methodDecl.name());
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
		addLine(String.format("ret %s %%_%d", TypeDecider.llvmType(methodDecl.returnType()), lastRegisterNumber - 1));
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
		String signature = funcSymbol.getDecl().split(Symbol.DECL_MAJOR_SEP)[0];
		String[] splitted = signature.split(Symbol.DECL_SEP);
		if (!signature.equals("")) {
			for (int i = 0; i < splitted.length; i++) {
				sig.append(", ");
				callArgs.append(", ");
				sig.append(TypeDecider.llvmType(splitted[i]));
				callArgs.append(TypeDecider.llvmType(splitted[i]));
				callArgs.append(" %_").append(actualsRegisters.get(i));
			}
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
		// String s = String.format("%%_%d = add i8* 0, %%this", lastRegisterNumber++);
		// addLine(s);
		int casted = lastRegisterNumber++;
		addLine(String.format("%%_%d = bitcast i8* %%this to i32*", casted));
		addLine(String.format("%%_%d = bitcast i32* %%_%d to i8*", lastRegisterNumber++, casted));
	}

	@Override
    public void visit(NewObjectExpr e) {
        int REGISTER_ALLOCATED = 3;
        // allocation
        int tmp0 = lastRegisterNumber + REGISTER_ALLOCATED - 1;
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
        
        lastRegisterNumber++; // allocation of the tmp0
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
		int labelNums[] = { lastLabelNumber++, lastLabelNumber++, lastLabelNumber++ };
		ifStatement.cond().accept(this);
		addLine("br i1 %_" + (lastRegisterNumber - 1) + ", " + "label %label" + labelNums[0] + ", label %label"
				+ labelNums[1]);
		builder.append("label" + labelNums[0] + ":\n");
		ifStatement.thencase().accept(this);
		addLine("br label %label" + labelNums[2] + "\n");
		builder.append("label" + labelNums[1] + ":\n");
		ifStatement.elsecase().accept(this);
		addLine("br label %label" + labelNums[2] + "\n");
		builder.append("label" + labelNums[2] + ":\n");
	}

	@Override
	public void visit(WhileStatement whileStatement) {
		int labelNums[] = { lastLabelNumber++, lastLabelNumber++, lastLabelNumber++ };
		addLine("br label %loop" + labelNums[0]);
		builder.append("loop" + labelNums[0] + ":"); // loop cond
		whileStatement.cond().accept(this);
		addLine("br i1 %_" + (lastRegisterNumber - 1) + ", label %loop" + labelNums[1] + ", label %loop" + labelNums[2]);
		builder.append("loop" + labelNums[1] + ":\n"); // loop body
		whileStatement.body().accept(this);
		addLine("br label %loop" + labelNums[0]);
		builder.append("loop" + labelNums[2] + ":\n"); // out of loop
	}

	// res[0] - the register that contains lv
	// res[1] - its type
	public String[] getLValue(String lvID, SymbolTable st) {
		Symbol var = st.varLookup(lvID);
		String type = TypeDecider.llvmType(var.getDecl());
		SymbolKind kind = var.getKind();
		String id = lvID;
		if (kind == SymbolKind.FIELD) {
			int offset = symbolMapping.get(var);
			addLine("%_" + lastRegisterNumber++ + " = getelementptr i8, i8* %this, i32 " + offset);
			addLine("%_" + lastRegisterNumber++ + " = bitcast i8* %_" + (lastRegisterNumber - 2) + " to " + type + "*");
			id = "_" + (lastRegisterNumber - 1);
		}
		String[] res = { id, type };
		return res;
	}

	// ARRAYS
	public void visit(AssignArrayStatement assignArrayStatement) {
		String[] lvData = getLValue(assignArrayStatement.lv(), assignArrayStatement.getSymbolTable()); // updated
		var arrayName = lvData[0]; // updated
		var arrayRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = load i32*, i32** %%%s", arrayRegister, arrayName));

		assignArrayStatement.index().accept(this);
		var indexRegister = lastRegisterNumber - 1;
		var indexComparisonToZeroRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = icmp slt i32 %%_%s, 0", indexComparisonToZeroRegister, indexRegister));

		var label1 = lastLabelNumber++;
		var label2 = lastLabelNumber++;
		addLine(String.format("br i1 %%_%s, label %%arr_check%s, label %%arr_check%s", indexComparisonToZeroRegister,
				label1, label2));
		addLabel(String.format("arr_check%s:", label1));
		addLine("call void @throw_oob()\n");
		addLine("br label %arr_check" + label2);
		addLabel(String.format("arr_check%s:", label2));

		var lengthPointerRegister = lastRegisterNumber++;
		var lengthRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = getelementptr i32, i32* %%_%s, i32 0", lengthPointerRegister, arrayRegister));
		addLine(String.format("%%_%s = load i32, i32* %%_%s", lengthRegister, lengthPointerRegister));

		var indexComparisonToLengthRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = icmp sle i32 %%_%s, %%_%s", indexComparisonToLengthRegister, lengthRegister,
				indexRegister));

		var label3 = lastLabelNumber++;
		var label4 = lastLabelNumber++;

		addLine(String.format("br i1 %%_%s, label %%arr_check%s, label %%arr_check%s", indexComparisonToLengthRegister,
				label3, label4));
		addLabel(String.format("arr_check%s:", label3));
		addLine("call void @throw_oob()");
		addLine(String.format("br label %%arr_check%s", label4));
		addLabel(String.format("arr_check%s:", label4));
		var indexPlusOneRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = add i32 %%_%s, 1", indexPlusOneRegister, indexRegister));

		var elementPointerRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = getelementptr i32, i32* %%_%s, i32 %%_%s", elementPointerRegister, arrayRegister,
				indexPlusOneRegister));

		assignArrayStatement.rv().accept(this);
		var assignValueRegister = lastRegisterNumber - 1;

		addLine(String.format("store i32 %%_%s, i32* %%_%s", assignValueRegister, elementPointerRegister));
	}

    @Override
    public void visit(ArrayAccessExpr e) {
		e.arrayExpr().accept(this);
		var arrayRegister = lastRegisterNumber - 1;
		e.indexExpr().accept(this);
		var indexRegister = lastRegisterNumber - 1;

		var indexComparisonToZeroRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = icmp slt i32 %%_%s, 0", indexComparisonToZeroRegister, indexRegister));

		var label1 = lastLabelNumber++;
		var label2 = lastLabelNumber++;
		addLine(String.format("br i1 %%_%s, label %%arr_check%s, label %%arr_check%s", indexComparisonToZeroRegister, label1, label2));
		addLabel(String.format("arr_check%s:", label1));
		addLine("call void @throw_oob()\n");
		addLine("br label %arr_check" + label2);
		addLabel(String.format("arr_check%s:", label2));

		var lengthPointerRegister = lastRegisterNumber++;
		var lengthRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = getelementptr i32, i32* %%_%s, i32 0", lengthPointerRegister, arrayRegister));
		addLine(String.format("%%_%s = load i32, i32* %%_%s", lengthRegister, lengthPointerRegister));

		var indexComparisonToLengthRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = icmp sle i32 %%_%s, %%_%s", indexComparisonToLengthRegister, lengthRegister, indexRegister));

		var label3 = lastLabelNumber++;
		var label4 = lastLabelNumber++;

		addLine(String.format("br i1 %%_%s, label %%arr_check%s, label %%arr_check%s", indexComparisonToLengthRegister, label3, label4));
		addLabel(String.format("arr_check%s:", label3));
		addLine("call void @throw_oob()");
		addLine(String.format("br label %%arr_check%s", label4));
		addLabel(String.format("arr_check%s:",label4));
		var indexPlusOneRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = add i32 %%_%s, 1", indexPlusOneRegister, indexRegister));

		var elementPointerRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = getelementptr i32, i32* %%_%s, i32 %%_%s", elementPointerRegister, arrayRegister, indexPlusOneRegister));
		var elementValueRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = load i32, i32* %%_%s", elementValueRegister, elementPointerRegister));
    }

    @Override
    public void visit(ArrayLengthExpr e) {
		e.arrayExpr().accept(this);
		var arrayRegister = lastRegisterNumber - 1;
		var lengthElementPointerRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = getelementptr i32, i32* %%_%s, i32 0", lengthElementPointerRegister, arrayRegister));
		var lengthRegister = lastRegisterNumber++;
		addLine(String.format("%%_%s = load i32, i32* %%_%s", lengthRegister, lengthElementPointerRegister));
    }

    @Override
    public void visit(NewIntArrayExpr e) {
		e.lengthExpr().accept(this);
		var lengthResultRegister = lastRegisterNumber - 1;
		var compareToZeroResultRegister = lastRegisterNumber++;

		addLine(String.format("%%_%s = icmp slt i32 %%_%s , 0", compareToZeroResultRegister, lengthResultRegister));

		var label1 = lastLabelNumber++;
		var label2 = lastLabelNumber++;


		addLine(String.format("br i1 %%_%s, label %%arr_alloc%s, label %%arr_alloc%s", compareToZeroResultRegister, label1, label2));
		addLabel(String.format("arr_alloc%s:",label1));
		addLine("call void @throw_oob()\n");
		addLine("br label %arr_alloc" + label2);
		addLabel(String.format("arr_alloc%s:",label2));

		var lengthPlusOneResultRegister = lastRegisterNumber++;

		addLine(String.format("%%_%s = add i32 %%_%s, 1", lengthPlusOneResultRegister, lengthResultRegister));

		var allocationRegister = lastRegisterNumber++;

		addLine(String.format("%%_%s = call i8* @calloc(i32 4, i32 %%_%s)", allocationRegister, lengthPlusOneResultRegister));

		var castRegister = lastRegisterNumber++;

		addLine(String.format("%%_%s = bitcast i8* %%_%s to i32*", castRegister, allocationRegister));

		addLine(String.format("store i32 %%_%s, i32* %%_%s", lengthResultRegister, castRegister));

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
		Symbol var = assignStatement.getSymbolTable().varLookup(assignStatement.lv());
		String type = TypeDecider.llvmType(var.getDecl()), rv;
		SymbolKind kind = var.getKind();
		String id = assignStatement.lv();
		assignStatement.rv().accept(this);
		rv = "%_" + (lastRegisterNumber - 1);
		if (kind == SymbolKind.FIELD) {
			int offset = symbolMapping.get(var);
			addLine("%_" + lastRegisterNumber++ + " = getelementptr i8, i8* %this, i32 " + offset);
			addLine("%_" + lastRegisterNumber++ + " = bitcast i8* %_" + (lastRegisterNumber - 2) + " to " + type + "*");
			id = "_" + (lastRegisterNumber - 1);
		}
		addLine("store " + type + " " + rv + ", " + type + "* " + "%" + id);
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
		int labelNums[] = {0, 0, 0, 0};
		getAndOperands(operands, e);
		operands.get(0).accept(this);
		addLine("br label %andcond" + lastLabelNumber);
		for (int i = 1; i < operands.size(); i++) {
			labelNums = new int[] { lastLabelNumber++, lastLabelNumber++, lastLabelNumber++, lastLabelNumber };
			builder.append("andcond" + labelNums[0] + ":\n");
			addLine("br i1 %_" + (lastRegisterNumber - 1) + ", label %andcond" + labelNums[1] + ", label %andcond"
					+ labelNums[2]);
			builder.append("andcond" + labelNums[1] + ":\n");
			operands.get(i).accept(this);
			addLine("br label %andcond" + labelNums[2]);
			builder.append("andcond" + labelNums[2] + ":\n");
			addLine("%_" + lastRegisterNumber++ + " = phi i1 [0, %andcond" + labelNums[0] + "], [%_"
					+ (lastRegisterNumber - 2) + ", %andcond" + labelNums[1] + "]");
			addLine("br label %andcond" + labelNums[3]);
		}
		builder.append("andcond" + labelNums[3] + ":\n");
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
		e.e().accept(this);
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
		Symbol var = e.getSymbolTable().varLookup(e.id());
		String type = TypeDecider.llvmType(var.getDecl());
		SymbolKind kind = var.getKind();
		String id = e.id();
		if (kind == SymbolKind.FIELD) {
			int offset = symbolMapping.get(var);
			addLine("%_" + lastRegisterNumber++ + " = getelementptr i8, i8* %this, i32 " + offset);
			addLine("%_" + lastRegisterNumber++ + " = bitcast i8* %_" + (lastRegisterNumber - 2) + " to " + type + "*");
			id = "_" + (lastRegisterNumber - 1);
		}
		addLine("%_" + lastRegisterNumber++ + " = load " + type + ", " + type + "* %" + id);
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
