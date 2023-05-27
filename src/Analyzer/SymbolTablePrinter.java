package Analyzer;

import java.util.stream.Collectors;

import Analyzer.SymbolTable.SymbolTable;
import Analyzer.Type.Type;
import Analyzer.SymbolTable.ClassSymbolTable;
import Analyzer.SymbolTable.GlobalSymbolTable;
import Analyzer.SymbolTable.RegularSymbolTable;
import Analyzer.Type.ClassType;
import Analyzer.Type.MethodType;

public class SymbolTablePrinter {
    // g_in MUST BE A GlobalSymbolTable xD
    public static void PrintGlobalSymbolTable(SymbolTable g_in) {
        GlobalSymbolTable g = (GlobalSymbolTable) g_in;
        for (String key : g.classes.keySet()) {
            ClassType curr = g.classes.get(key);
            if (curr.parents.isEmpty())
                System.out.printf("class %s\n", key);
            else
                System.out.printf("class %s inherits from: %s\n", key, curr.parents.toString());
            
            PrintClassSymbolTable(curr.st);
        }
    }

    private static void PrintClassSymbolTable(ClassSymbolTable g) {
        // print fields
        for (String key : g.fields.symbols.keySet()) {
            System.out.printf("    %s : %s\n", key, g.fields.symbols.get(key).toString());
        }

        // print methods
        for (String key : g.methods.keySet()) {
            MethodType c = g.methods.get(key);
            System.out.printf("    %s (", key);
            String pStr = c.params.stream()
                .map(Type::toString)
                .collect(Collectors.joining(", "));
            System.out.printf(pStr);
            System.out.printf (") returns %s\n", c.retType.toString());
            PrintMethodSymbolTable(c.st);
        }
    }

    private static void PrintMethodSymbolTable(RegularSymbolTable g) {
        for (String key : g.symbols.keySet()) {
            System.out.printf("        %s : %s\n", key, g.symbols.get(key).toString());
        }
    }
}