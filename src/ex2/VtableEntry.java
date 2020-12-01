package ex2;

import ast.MethodDecl;

public class VtableEntry implements Comparable<VtableEntry>{
    private final int index;
    private String className;
    private final String funcName;
    private final MethodDecl methodDecl;

    public VtableEntry(int index, String className, String funcName, MethodDecl methodDecl) {
        this.index = index;
        this.className = className;
        this.funcName = funcName;
        this.methodDecl = methodDecl;
    }

    public int getIndex() {
        return index;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public MethodDecl getMethodDecl() {
        return methodDecl;
    }

    public String getFuncName() {
        return funcName;
    }

    @Override
    public int compareTo(VtableEntry o) {
        return this.index - o.index;
    }
}
