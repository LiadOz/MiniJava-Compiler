import ast.*;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            var inputFilename = args[0];
            FileReader fileReader = new FileReader(new File(inputFilename));
        
            Parser p = new Parser(new Lexer(fileReader));
            Expr expr = (Expr) p.parse().value;

            AstPrintVisitor astPrinter = new AstPrintVisitor();
            expr.accept(astPrinter);
            System.out.println(astPrinter.getString());
            
        } catch (Exception e) {
            System.out.println("General error: " + e);
            e.printStackTrace();
        }
    }
}
