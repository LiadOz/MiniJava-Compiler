package ast;

import javax.xml.bind.annotation.XmlElement;

public abstract class BinaryExpr extends Expr {
    private Expr e1;
    private Expr e2;

    // for deserialization only!
    public BinaryExpr() {
    }

    public BinaryExpr(Expr e1, Expr e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public Expr e1() {
        return e1;
    }

    public Expr e2() {
        return e2;
    }
}
