package ex3;

import ast.*;
import ex1.ClassMapping;

public class VariableInitVisitor implements Visitor {

	// Point 15 and the appendix at the end of the exercise

	private final ClassMapping classMap;
	private Lattice lattice;

	public VariableInitVisitor(ClassMapping classMap) {
		this.classMap = classMap;
		this.lattice = new Lattice();
	}

	@Override
	public void visit(Program program) {
		program.mainClass().accept(this);
		for (ClassDecl classdecl : program.classDecls()) {
			classdecl.accept(this);
		}
	}

	@Override
	public void visit(ClassDecl classDecl) {
		for (var methodDecl : classDecl.methoddecls()) {
			methodDecl.accept(this);
		}
	}

	@Override
	public void visit(MainClass mainClass) {
//    	mainClass.argsName();
		mainClass.mainStatement().accept(this);
	}

	@Override
	public void visit(MethodDecl methodDecl) {
//    	for (var formal : methodDecl.formals()) {
//            formal.accept(this);
//        }
		for (var varDecl : methodDecl.vardecls()) {
			varDecl.accept(this);
		}
		for (var stmt : methodDecl.body()) {
			stmt.accept(this);
		}
		methodDecl.ret().accept(this);
	}

	@Override
	public void visit(FormalArg formalArg) {
//    	formalArg.name();
	}

	@Override
	public void visit(VarDecl varDecl) {
		lattice.add(varDecl.name(), initKind.ff);
	}

	@Override
	public void visit(BlockStatement blockStatement) {
		for (var s : blockStatement.statements()) {
			s.accept(this);
		}
	}

	@Override
	public void visit(IfStatement ifStatement) {
		Lattice thenLat, elseLat;
		thenLat = new Lattice();
		elseLat = new Lattice();
		thenLat.copy(lattice);
		elseLat.copy(lattice);
		ifStatement.cond().accept(this);
		lattice = thenLat;
		lattice.assign("y");
		ifStatement.thencase().accept(this);
		lattice = elseLat;
		ifStatement.elsecase().accept(this);
		lattice.join(thenLat);
	}

	@Override
	public void visit(WhileStatement whileStatement) {
		Lattice origLat = new Lattice();
		origLat.copy(lattice);
		whileStatement.cond().accept(this);
		whileStatement.body().accept(this);
		lattice.join(origLat);
	}

	@Override
	public void visit(SysoutStatement sysoutStatement) {
		sysoutStatement.arg().accept(this);
	}

	@Override
	public void visit(AssignStatement assignStatement) {
		assignStatement.rv().accept(this);

		lattice.assign(assignStatement.lv());
	}

	@Override
	public void visit(AssignArrayStatement assignArrayStatement) {
		assignArrayStatement.rv().accept(this);
		assignArrayStatement.index().accept(this);

		lattice.assign(assignArrayStatement.lv());
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
		boolean isInit = lattice.isInit(e.id());
		if (!isInit) {
			throw new SemanticException(String.format("Error: variable %s may not have been initialized", e.id()));
		}
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

	}
}
