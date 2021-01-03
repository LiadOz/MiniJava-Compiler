package ast;

public class AstPrintVisitor implements Visitor {
    private StringBuilder builder = new StringBuilder();

    private int indent = 0;

    public String getString() {
        return builder.toString();
    }

    private void appendWithIndent(String str) {
        builder.append("\t".repeat(indent));
        builder.append(str);
    }

    private void visitBinaryExpr(BinaryExpr e, String infixSymbol) {
        builder.append("(");
        e.e1().accept(this);
        builder.append(")");
        builder.append(" " + infixSymbol + " ");
        builder.append("(");
        e.e2().accept(this);
        builder.append(")");
    }

    @Override
    public void visit(AddExpr e) {
        visitBinaryExpr(e, "+");;
    }

    @Override
    public void visit(SubtractExpr e) {
        visitBinaryExpr(e, "-");
    }

    @Override
    public void visit(MultExpr e) {
        visitBinaryExpr(e, "*");
    }

    @Override
    public void visit(DivExpr e) {
        visitBinaryExpr(e, "/");
    }

    @Override
    public void visit(MinusExpr e) {
        builder.append("-(");
        e.e().accept(this);
        builder.append(")");
    }

    @Override
    public void visit(MaxExpr e) {
        builder.append("max(");
        var sep="";
        for (var arg: e.args()) {
            builder.append(sep);
            arg.accept(this);
            sep=", ";
        }
        builder.append(")");
    }

    @Override
    public void visit(IntegerLiteralExpr e) {
        builder.append(e.num());
    }
}
