package Analyzer;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    Map<String, List<String>> parentToChildren;
    Set<String> rootClasses;

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
        ClassVisitor v = new ClassVisitor();
        good = v.activate(program, g) && good;
        parentToChildren = v.parentToChildrens();
        rootClasses = v.rootClasses();
        good = new FieldVisitor().activate(program, g) && good;
        good = new MethodVisitor().activate(program, g) && good;
        good = new StatementVisitor().activate(program, g) && good;
        return g;
    }

    public Program getProgram() {
        return (Program) root.value;
    }

    public Map<String, List<String>> parentToChildrens() {
        return parentToChildren;
      }
    public Set<String> rootClasses() {
        return rootClasses;
    }
    public boolean valid() { return good; }
}
