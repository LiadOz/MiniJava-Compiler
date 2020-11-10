package ex1;

import ast.AstNode;

public class Symbol {
    private String id; // name of symbol
    private SymbolKind kind;
    private String decl; // static type for variables
    private AstNode node;

    public Symbol(String id, SymbolKind kind, String decl, AstNode node) {
        this.id = id;
        this.kind = kind;
        this.decl = decl;
        this.node = node;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SymbolKind getKind() {
        return kind;
    }

    public String getDecl() {
        return decl;
    }

    public AstNode getNode() {
        return node;
    }
}
