package Compiler;

import AST.Program;
import Analyzer.SymbolTable.GlobalSymbolTable;

public class compiler {
    GlobalSymbolTable g;
    Program p;
    public compiler(GlobalSymbolTable g, Program p) {
        this.g = g;
        this.p = p;
    }

    public boolean compile() {
        CodegenVisitor v = new CodegenVisitor();
        v.activate(p, g);
        return true;
    }
}
