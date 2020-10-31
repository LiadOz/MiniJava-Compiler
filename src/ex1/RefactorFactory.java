package ex1;

public class RefactorFactory {
    public RefactorProgram createRefactor(String renameType){
        if (renameType.equals("method"))
            return new RefactorMethod();
        else if (renameType.equals("var"))
            return new RefactorVariable();
        else
            throw new IllegalArgumentException("No reformat for type" + renameType);
    }
}
