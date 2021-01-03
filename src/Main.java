import ast.AstPrintVisitor;
import ast.AstXMLSerializer;
import ast.Program;
import ex1.RefactorFactory;
import ex1.RefactorProgram;
import ex2.ProgramCompiler;
import ex3.SemanticChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        try {
            var inputMethod = args[0];
            var action = args[1];
            var filename = args[args.length - 2];
            var outfilename = args[args.length - 1];

            Program prog;

            if (inputMethod.equals("parse")) {
                FileReader reader = new FileReader(new File(filename));
                Parser parser = new Parser(new Lexer(reader));
                prog = (Program)parser.parse().value;
            } else if (inputMethod.equals("unmarshal")) {
                AstXMLSerializer xmlSerializer = new AstXMLSerializer();
                prog = xmlSerializer.deserialize(new File(filename));
            } else {
                throw new UnsupportedOperationException("unknown input method " + inputMethod);
            }

            var outFile = new PrintWriter(outfilename);
            try {

                if (action.equals("marshal")) {
                    AstXMLSerializer xmlSerializer = new AstXMLSerializer();
                    xmlSerializer.serialize(prog, outfilename);
                } else if (action.equals("print")) {
                    AstPrintVisitor astPrinter = new AstPrintVisitor();
                    astPrinter.visit(prog);
                    outFile.write(astPrinter.getString());

                } else if (action.equals("semantic")) {
                    SemanticChecker semCheck = new SemanticChecker();
                    outFile.write(semCheck.check(prog));
                } else if (action.equals("compile")) {
                    ProgramCompiler compile = new ProgramCompiler();
                    outFile.write(compile.compile(prog));

                } else if (action.equals("rename")) {
                    var type = args[2];
                    var originalName = args[3];
                    var originalLine = args[4];
                    var newName = args[5];

                    RefactorProgram refactorer;
                    if (type.equals("var") || type.equals("method")) {
                        refactorer = new RefactorFactory().createRefactor(type);
                    } else {
                        throw new IllegalArgumentException("unknown rename type " + type);
                    }
                    refactorer.refactor(prog, originalName, originalLine, newName);
                    AstXMLSerializer xmlSerializer = new AstXMLSerializer();
                    xmlSerializer.serialize(prog, outfilename);

                } else {
                    throw new IllegalArgumentException("unknown command line action " + action);
                }
            } finally {
                outFile.flush();
                outFile.close();
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error reading file: " + e);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("General error: " + e);
            e.printStackTrace();
        }
    }
}
