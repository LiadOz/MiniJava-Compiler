package ast;

public interface Visitor {
    public void visit(IntegerLiteralExpr e);
    public void visit(AddExpr e);
    public void visit(SubtractExpr e);
    public void visit(MultExpr e);
    public void visit(DivExpr e);
    public void visit(MinusExpr e);
    public void visit(MaxExpr e);
}
