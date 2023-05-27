package Analyzer.SymbolTable;

import Analyzer.Type.Type;



public abstract class SymbolTable {
    // called on a variable id
    // returns null if not found
    abstract Type Lookup(String id);
    
    abstract boolean Enter(String id, Type typ);
}
