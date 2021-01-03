package ast;

public class DivExpr extends BinaryExpr {

    // for deserialization only!
    public DivExpr() {
    }

    public DivExpr(Expr e1, Expr e2) {
        super(e1, e2);
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
