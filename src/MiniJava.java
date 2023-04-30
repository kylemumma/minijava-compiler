import Scanner.*;
import Parser.*;
import AST.*;
import AST.Visitor.*;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java.util.*;
import java.io.*;

public class MiniJava {
    public static void main(String[] args) {
        // args[0] = -S
        // args[1] = "<filename>.java"
        if (args.length != 2) {
            System.err.println("Usage: java MiniJava <option> <filename>");
            System.exit(1);
        } else if (args[0].equals("-S")) {
            scan(args[1]);
        } else if (args[0].equals("-A")) {
            parse(args[1], new ASTPrintVisitor());
        } else if (args[0].equals("-P")) {
            parse(args[1], new PrettyPrintVisitor());
        } else {
            System.err.println("Usage: java MiniJava <option> <filename>");
            System.exit(1);
        }
        
        
    }

    static void scan(String filename) {
        try {
            // create a scanner on the input file
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            InputStream istream = new FileInputStream(filename);
            Reader in = new BufferedReader(new InputStreamReader(istream));
            scanner s = new scanner(in, sf);
            // will get set to 1 if there are any syntax errors found while scanning
            int syntaxErr = 0;
            Symbol t = s.next_token();
            while (t.sym != sym.EOF) { 
                if (t.sym == sym.error)
                    syntaxErr = 1;

                // print each token that we scan
                System.out.print(s.symbolToString(t) + " ");
                t = s.next_token();
            }
            System.out.println("");
            System.exit(syntaxErr);
        } catch (Exception e) {
            // yuck: some kind of error in the compiler implementation
            // that we're not expecting (a bug!)
            System.err.println("Unexpected internal compiler error: " + 
                        e.toString());
            // print out a stack dump
            e.printStackTrace();
            System.exit(1);
        }
    }
    static void parse(String filename, Visitor v) {
        try {
            // create a scanner on the input file
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            InputStream istream = new FileInputStream(filename);
            Reader in = new BufferedReader(new InputStreamReader(istream));
            scanner s = new scanner(in, sf);
            parser p = new parser(s, sf);
            Symbol root;
            // replace p.parse() with p.debug_parse() in the next line to see
            // a trace of parser shift/reduce actions during parsing
            root = p.parse();
            // We know the following unchecked cast is safe because of the
            // declarations in the CUP input file giving the type of the
            // root node, so we suppress warnings for the next assignment.
            
            @SuppressWarnings("unchecked")
            Program program = (Program)root.value;
            program.accept(v);
            System.out.println();
        } catch (Exception e) {
            // yuck: some kind of error in the compiler implementation
            // that we're not expecting (a bug!)
            System.err.println("Unexpected internal compiler error: " + 
                               e.toString());
            // print out a stack dump
            e.printStackTrace();
            System.exit(1);
        }
    }
}