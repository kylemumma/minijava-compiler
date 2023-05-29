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
        genVtables();
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
            //mt.sz = (mt.st.symbols.size() + 1) * 8; // size of stack frame
            //if (mt.sz % 16 != 0) mt.sz += 8;
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

    private void genVtables() {
        for (String s : rootClasses) {
            genVtableFor((ClassType)g.Lookup(s), new ArrayList<String>());
        }
    }

    private void genVtableFor(ClassType ct, List<String> methods) {
        for (String k : ct.st.methods.keySet()) {
            MethodType mt = ct.st.methods.get(k);
            int idx = mt.offset/8;
            while(methods.size() < idx){methods.add(null);}
            methods.set(idx-1, ct.name + "$" + mt.name);
        }
        if(ct.parent == null) {
            System.out.println(ct.name + "$$: " + ".quad 0");
        } else {
            System.out.println(ct.name + "$$: " + ".quad " + ct.parent.name + "$$");
        }
    
        for (String s : methods) {
            System.out.println(".quad " + s);
        }
        System.out.println("");
        
        for (String child : parentToChildren.getOrDefault(ct.name, new ArrayList<String>())) { 
            genVtableFor((ClassType)g.Lookup(child), methods);
        }
      }
}
