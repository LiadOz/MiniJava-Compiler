package ast;

import ex1.SymbolTable;

import javax.xml.bind.annotation.XmlElement;

public abstract class AstNode {
    @XmlElement(required = false)
    public Integer lineNumber;
    private SymbolTable symbolTable;

    public AstNode() {
        lineNumber = null;
    }

    public AstNode(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    abstract public void accept(Visitor v);
}
