package ex1;

import ast.AstNode;

public class Symbol {
    public static final String DECL_SEP = ",";
    public static final String DECL_MAJOR_SEP= "->";
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

    public String getReturnType(){
        if(kind != SymbolKind.METHOD){
            throw new UnsupportedOperationException();
        }
        return decl.split(Symbol.DECL_MAJOR_SEP)[1];
    }

    public String[] getArgumentTypes(){
        if(kind != SymbolKind.METHOD){
            throw new UnsupportedOperationException();
        }
        String rightSide = decl.split(Symbol.DECL_MAJOR_SEP)[0];
        if (rightSide.equals(""))
            return new String[0];
        return rightSide.split(Symbol.DECL_SEP);
    }
}
