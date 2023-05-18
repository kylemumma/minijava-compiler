package Analyzer;

public class SymbolTablePrinter {
    // g_in MUST BE A GlobalSymbolTable xD
    public static void PrintGlobalSymbolTable(SymbolTable g_in) {
        GlobalSymbolTable g = (GlobalSymbolTable) g_in;
        for (String key : g.classes.keySet()) {
            ClassType curr = g.classes.get(key);
            System.out.printf("%s inherits from: %s", key, curr.parents.toString());
        }
    }
}