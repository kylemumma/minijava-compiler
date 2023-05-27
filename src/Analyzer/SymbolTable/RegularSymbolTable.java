package Analyzer.SymbolTable;

import java.util.HashMap;
import java.util.Map;

import Analyzer.Type.Type;
import Analyzer.Type.UnknownType;

public class RegularSymbolTable extends SymbolTable {
    public RegularSymbolTable parent;
    public Map<String, Type> symbols;

    // has parent if in method scope (not class fields)
    public RegularSymbolTable() {
        symbols = new HashMap<String, Type>();
    }

    public RegularSymbolTable(RegularSymbolTable parent) {
        this();
        this.parent = parent;
    }

    // looking for variable name
    public Type Lookup(String id) {
        Type ret = symbols.get(id);
        if (ret == null) {
            if (parent == null) {
                return new UnknownType();
            }
            else {
                // callers job to figure out if this method in same class or parent
                return parent.Lookup(id);
            }
        } else {
            return ret;
        }
    }
    public boolean Enter(String id, Type typ) {
        if (symbols.containsKey(id)) {
            return false;
        }
        symbols.put(id, typ);
        return true;
    }
}