package Analyzer;

import java.util.HashMap;
import java.util.Map;



public abstract class SymbolTable {
    // called on a variable id
    // returns null if not found
    abstract Type Lookup(String id);
    
    abstract boolean Enter(String id, Type typ);
}

// contains information of all classes in program
class GlobalSymbolTable extends SymbolTable {
    // string to ClassSymbolTable types
    Map<String, ClassType> classes;

    GlobalSymbolTable() {
        classes = new HashMap<String, ClassType>();
    }

    // lookup for class name
    Type Lookup(String id) {
        ClassType ret = classes.get(id);
        if (ret == null) {
            return new UnknownType();
        } else {
            return ret;
        }
    }

    boolean Enter(String id, Type typ) {
        Type old = classes.put(id, (ClassType)typ);
        // returns false if there was a previous element with same name
        return old == null;
    }
}

class ClassSymbolTable extends SymbolTable {
    ClassSymbolTable parent; // another class symbol table or null
    // string to RegularSymbolTable types
    Map<String, MethodType> methods;
    RegularSymbolTable fields; // should never have a parent, but is parent to method scoped shit

    ClassSymbolTable() {
        parent = null;
        methods = new HashMap<String, MethodType>();
        fields = new RegularSymbolTable();
    }

    // lookup for methodname
    Type Lookup(String id) {
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

    boolean Enter(String id, Type typ) {
        if (methods.containsKey(id)) {
            return false;
        }
        methods.put(id, (MethodType)typ);
        return true;
    }
}

class RegularSymbolTable extends SymbolTable {
    RegularSymbolTable parent;
    Map<String, Type> symbols;

    // has parent if in method scope (not class fields)
    RegularSymbolTable() {
        symbols = new HashMap<String, Type>();
    }

    RegularSymbolTable(RegularSymbolTable parent) {
        this();
        this.parent = parent;
    }

    // looking for variable name
    Type Lookup(String id) {
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
    boolean Enter(String id, Type typ) {
        if (symbols.containsKey(id)) {
            return false;
        }
        symbols.put(id, typ);
        return true;
    }
}
