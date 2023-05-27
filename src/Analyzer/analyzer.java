package Analyzer;

import AST.Program;
import Analyzer.SymbolTable.GlobalSymbolTable;
import Analyzer.SymbolTable.SymbolTable;
import Parser.parser;
import java_cup.runtime.Symbol;

public class analyzer {
    // root of AST
    Symbol root;
    boolean good = true;
    Program p;

    public analyzer(parser p) throws Exception {
        // replace p.parse() with p.debug_parse() in the next line to see
        // a trace of parser shift/reduce actions during parsing
        root = p.parse();
    }

    public SymbolTable analyze() {
        // We know the following unchecked cast is safe because of the
        // declarations in the CUP input file giving the type of the
        // root node, so we suppress warnings for the next assignment.
        
        Program program = (Program)root.value;
        GlobalSymbolTable g = new GlobalSymbolTable();
        good = new ClassVisitor().activate(program, g) && good;
        good = new FieldVisitor().activate(program, g) && good;
        good = new MethodVisitor().activate(program, g) && good;
        good = new StatementVisitor().activate(program, g) && good;
        return g;
    }

    public Program getProgram() {
        return (Program) root.value;
    }

    public boolean valid() { return good; }
}
