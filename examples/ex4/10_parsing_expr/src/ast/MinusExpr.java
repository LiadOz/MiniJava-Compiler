package ast;

import javax.xml.bind.annotation.XmlElement;

public class MinusExpr extends Expr {
    private Expr e;

    // for deserialization only!
    public MinusExpr() {
    }

    public MinusExpr(Expr e) {
        this.e = e;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public Expr e() {
        return e;
    }
}
