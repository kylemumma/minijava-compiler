package Analyzer;

import AST.Program;
import AST.Visitor.Visitor;
import Parser.parser;
import java_cup.runtime.Symbol;

public class analyzer {
    // root of AST
    Symbol root;

    public analyzer(parser p) {
        try {
            // replace p.parse() with p.debug_parse() in the next line to see
            // a trace of parser shift/reduce actions during parsing
            root = p.parse();
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

    public void analyze() {
        // We know the following unchecked cast is safe because of the
        // declarations in the CUP input file giving the type of the
        // root node, so we suppress warnings for the next assignment.
        
        @SuppressWarnings("unchecked")
        Program program = (Program)root.value;
        GlobalSymbolTable g = new GlobalSymbolTable();
        new ClassVisitor().activate(program, g);
        new MethodVisitor().activate(program, g);
        new FieldVisitor().activate(program, g);
        System.out.println();
        System.exit(0);
    }
}
