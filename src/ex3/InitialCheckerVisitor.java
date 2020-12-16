package ex3;

import ast.*;

import java.util.HashSet;
import java.util.Set;

// checks things so you can use SymbolTables
// solves points 1-3
public class InitialCheckerVisitor implements Visitor {
    // TODO figure out how can cycles be created

    @Override
    public void visit(Program program) {
        Set<String> classes = new HashSet<String>();
        String mainClass = program.mainClass().name();
        classes.add(mainClass); // used to prevent duplicate class name

        for (ClassDecl classDecl : program.classDecls()) {
            var superName = classDecl.superName();
            if (classes.contains(classDecl.name()))
                throw new SemanticException("duplicate class name"); // Point 3

            classes.add(classDecl.name()); // it's here to catch if the super_name == name

            if (superName != null) {
                if (!classes.contains(superName))
                    throw new SemanticException("invalid super class"); // Point 1

                if (mainClass.equals(superName))
                    throw new SemanticException("invalid super name"); // Point 2
            }
        }
    }

    @Override
    public void visit(ClassDecl classDecl) {

    }

    @Override
    public void visit(MainClass mainClass) {

    }

    @Override
    public void visit(MethodDecl methodDecl) {

    }

    @Override
    public void visit(FormalArg formalArg) {

    }

    @Override
    public void visit(VarDecl varDecl) {

    }

    @Override
    public void visit(BlockStatement blockStatement) {

    }

    @Override
    public void visit(IfStatement ifStatement) {

    }

    @Override
    public void visit(WhileStatement whileStatement) {

    }

    @Override
    public void visit(SysoutStatement sysoutStatement) {

    }

    @Override
    public void visit(AssignStatement assignStatement) {

    }

    @Override
    public void visit(AssignArrayStatement assignArrayStatement) {

    }

    @Override
    public void visit(AndExpr e) {

    }

    @Override
    public void visit(LtExpr e) {

    }

    @Override
    public void visit(AddExpr e) {

    }

    @Override
    public void visit(SubtractExpr e) {

    }

    @Override
    public void visit(MultExpr e) {

    }

    @Override
    public void visit(ArrayAccessExpr e) {

    }

    @Override
    public void visit(ArrayLengthExpr e) {

    }

    @Override
    public void visit(MethodCallExpr e) {

    }

    @Override
    public void visit(IntegerLiteralExpr e) {

    }

    @Override
    public void visit(TrueExpr e) {

    }

    @Override
    public void visit(FalseExpr e) {

    }

    @Override
    public void visit(IdentifierExpr e) {

    }

    @Override
    public void visit(ThisExpr e) {

    }

    @Override
    public void visit(NewIntArrayExpr e) {

    }

    @Override
    public void visit(NewObjectExpr e) {

    }

    @Override
    public void visit(NotExpr e) {

    }

    @Override
    public void visit(IntAstType t) {

    }

    @Override
    public void visit(BoolAstType t) {

    }

    @Override
    public void visit(IntArrayAstType t) {

    }

    @Override
    public void visit(RefType t) {

    }
}
