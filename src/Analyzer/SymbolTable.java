package Analyzer;

import Analyzer.Type;
import java.util.Map;



public abstract class SymbolTable {
    // called on a variable id
    // returns null if not found
    abstract Type Lookup(String id);
    
    abstract boolean Enter(String id, Type typ);
}

// contains information of all classes in program
class GlobalSymbolTable extends SymbolTable {
    SymbolTable parent;
    // string to ClassSymbolTable types
    Map<String, ClassType> classes;

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
    SymbolTable parent;
    // string to RegularSymbolTable types
    Map<String, MethodType> methods;
    RegularSymbolTable fields;

    // lookup for method
    Type Lookup(String id) {
        MethodType ret = methods.get(id);
        if (ret == null) {
            return new UnknownType();
        } else {
            return ret;
        }
    }

    Type LookupField(String id) {
        return fields.Lookup(id);
    }

    boolean Enter(String id, Type typ) {
        Type old = methods.put(id, (MethodType)typ);
        // returns false if there was a previous element with same name
        return old == null;
    }
}

class RegularSymbolTable extends SymbolTable {
    SymbolTable parent;
    Map<String, Type> symbols;

    Type Lookup(String id) {
        Type ret = symbols.get(id);
        if (ret == null) {
            return new UnknownType();
        } else {
            return ret;
        }
    }
    boolean Enter(String id, Type typ) {
        Type old = symbols.put(id, typ);
        // returns false if there was a previous element with same name
        return old == null;
    }
}
