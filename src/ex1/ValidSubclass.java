package ex1;

import ast.ClassDecl;
import ast.Program;

import java.util.HashSet;
import java.util.Set;

/*
    This class is used to determine if some class subclasses for an initial class.
    since MiniJava always defines subclasses after its parent you need to add nodes in sequential order.
 */
public class ValidSubclass {
    private final Set<String> baseNames;
    public ValidSubclass(ClassDecl initialClass) {
        baseNames = new HashSet<String>();
        baseNames.add((initialClass.name()));
    }

    public void initProg(Program prog){
        prog.accept(new SubclassVisitor(this));
    }

    // try to add class to valid subclasses
    public void add(ClassDecl nextClass) {
        if (baseNames.contains(nextClass.superName())){
            baseNames.add(nextClass.name());
        }
    }

    public boolean isSubclass(ClassDecl subclass){
        return baseNames.contains((subclass.name()));
    }

}
