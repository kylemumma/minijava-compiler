package Analyzer;

import AST.Program;
import AST.Visitor.Visitor;
import Parser.parser;
import java_cup.runtime.Symbol;

public class analyzer {
    // root of AST
    Symbol root;

    public analyzer(parser p) throws Exception {
        // replace p.parse() with p.debug_parse() in the next line to see
        // a trace of parser shift/reduce actions during parsing
        root = p.parse();
    }

    public GlobalSymbolTable analyze() {
        // We know the following unchecked cast is safe because of the
        // declarations in the CUP input file giving the type of the
        // root node, so we suppress warnings for the next assignment.
        
        @SuppressWarnings("unchecked")
        Program program = (Program)root.value;
        GlobalSymbolTable g = new GlobalSymbolTable();
        new ClassVisitor().activate(program, g);
        new FieldVisitor().activate(program, g);
        new MethodVisitor().activate(program, g);
        new StatementVisitor().activate(program, g);
        return g;
    }
}
