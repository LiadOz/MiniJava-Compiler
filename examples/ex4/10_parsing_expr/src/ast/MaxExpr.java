package ast;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class MaxExpr extends Expr {
    @XmlElement(required = true)
    private List<Expr> args;

    // for deserialization only!
    public MaxExpr() {
    }

    public MaxExpr(List<Expr> args) {
        this.args = args;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public List<Expr> args() {
        return args;
    }
}
