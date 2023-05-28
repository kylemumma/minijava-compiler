package Compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AST.Program;
import Analyzer.SymbolTable.GlobalSymbolTable;
import Analyzer.Type.*;
import Analyzer.SymbolTable.*;


public class compiler {
    GlobalSymbolTable g;
    Program p;
    Map<String, List<String>> parentToChildren;
    Set<String> rootClasses;
    public compiler(GlobalSymbolTable g, Program p, Map<String, List<String>> parentToChildren, Set<String> rootClasses) {
        this.g = g;
        this.p = p;
        this.parentToChildren = parentToChildren;
        this.rootClasses = rootClasses;
    }

    public boolean compile() {
        setLocations();
        CodegenVisitor v = new CodegenVisitor();
        v.activate(p, g);
        return true;
    }

      // for filling out parents list of a class
    private void dfs(String c, int fieldOffset, int methodOffset, ClassType parent) {
        ClassType curr = (ClassType)g.Lookup(c);
        RegularSymbolTable fields = curr.st.fields;
        
        // set offset of each field's type
        for (Type t : fields.symbols.values()) {
            t.offset = fieldOffset;
            fieldOffset += 8;
        }
        curr.sz = fieldOffset; // size of an instantiated class variable

        for (MethodType mt : curr.st.methods.values()) {
            // check if overridden method
            if (parent != null) {
                Type res = parent.st.Lookup(mt.name);
                if (res instanceof MethodType) {
                    MethodType og = (MethodType)res;
                    mt.offset = res.offset;
                } else {
                    mt.offset = methodOffset;
                    methodOffset += 8;
                }
            } else {
                mt.offset = methodOffset;
                methodOffset += 8;
            }
            // always start at 8 for local method variables
            int localoffset = 0;
            for (Type t : mt.st.symbols.values()) {
                t.offset = localoffset;
                localoffset += 8;
            }
            mt.sz = localoffset; // size of stack frame
            if (mt.sz % 16 != 0) mt.sz += 8;
        }


        for (String child : parentToChildren.getOrDefault(c, new ArrayList<String>())) { 
            dfs(child, fieldOffset, methodOffset, curr);
        }
    }

    void setLocations() {
        for (String s : rootClasses) {
            dfs(s, 8, 8, null);
        }
    }
}
