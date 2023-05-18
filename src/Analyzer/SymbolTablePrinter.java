public class SymbolTablePrinter {
    static void PrintGlobalSymbolTable(GlobalSymbolTable g) {
        for (String key : g.classes.keySet()) {
            ClassType curr = g.classes.get(key);
            System.out.printf("%s inherits from: %s", key, curr.parents.toString());
            
        }
    }
}