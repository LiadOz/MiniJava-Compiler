package ex3;

import ast.*;
import ex1.ClassMapping;
import ex1.SymbolKind;

public class VariableInitVisitor implements Visitor {

	// Point 15 and the appendix at the end of the exercise

	private Lattice lattice;


	public VariableInitVisitor() {
		this.lattice = new Lattice();
	}

	public VariableInitVisitor(Lattice l){
		this.lattice = l;
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
			lattice = new Lattice();
			methodDecl.accept(this);
		}
	}

	@Override
	public void visit(MainClass mainClass) {
		lattice = new Lattice();
		mainClass.mainStatement().accept(this);
	}

	@Override
	public void visit(MethodDecl methodDecl) {
		for(var formal: methodDecl.formals()){
			formal.accept(this);
		}

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
		lattice.assign(formalArg.name());
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
		var thenLat = new Lattice();
		var elseLat = new Lattice();
		thenLat.copy(lattice);
		elseLat.copy(lattice);
		var thenVisitor = new VariableInitVisitor(thenLat);
		var elseVisitor = new VariableInitVisitor(elseLat);
		ifStatement.cond().accept(this);
		ifStatement.thencase().accept(thenVisitor);
		ifStatement.elsecase().accept(elseVisitor);
		lattice = elseVisitor.lattice;
		lattice.join(thenVisitor.lattice);

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
		if(!lattice.isInit(assignArrayStatement.lv())){
			throw new SemanticException(String.format("Error: variable %s may not have been initialized", assignArrayStatement.lv()));
		}
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
	public void visit(MethodCallExpr e) {
		for (Expr arg : e.actuals()) {
			arg.accept(this);
		}
		e.ownerExpr().accept(this);
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
		e.lengthExpr().accept(this);
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
