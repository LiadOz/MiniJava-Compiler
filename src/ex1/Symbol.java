package ex1;

import ast.AstNode;

public class Symbol {
    private String id;
    private SymbolKind kind;
    private String decl;
    private AstNode node;

    public Symbol(String id, SymbolKind kind, String decl, AstNode node) {
        this.id = id;
        this.kind = kind;
        this.decl = decl;
        this.node = node;
    }
}
