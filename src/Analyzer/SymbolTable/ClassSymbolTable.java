package Analyzer.SymbolTable;

import java.util.HashMap;
import java.util.Map;

import Analyzer.Type.MethodType;
import Analyzer.Type.Type;
import Analyzer.Type.UnknownType;

public class ClassSymbolTable extends SymbolTable {
    public ClassSymbolTable parent; // another class symbol table or null
    // string to RegularSymbolTable types
    public Map<String, MethodType> methods;
    public RegularSymbolTable fields; // should never have a parent, but is parent to method scoped shit

    public ClassSymbolTable() {
        parent = null;
        methods = new HashMap<String, MethodType>();
        fields = new RegularSymbolTable();
    }

    // lookup for methodname
    public Type Lookup(String id) {
        MethodType ret = methods.get(id);
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

    // lookup for methodname only from this class
    public Type LookupHere(String id) {
        MethodType ret = methods.get(id);
        if (ret == null) {
            return new UnknownType();
        } else {
            return ret;
        }
    }

    public boolean Enter(String id, Type typ) {
        if (methods.containsKey(id)) {
            return false;
        }
        methods.put(id, (MethodType)typ);
        return true;
    }
}