package Analyzer.SymbolTable;

import java.util.HashMap;
import java.util.Map;

import Analyzer.Type.ClassType;
import Analyzer.Type.Type;
import Analyzer.Type.UnknownType;

// contains information of all classes in program
public class GlobalSymbolTable extends SymbolTable {
    // string to ClassSymbolTable types
    public Map<String, ClassType> classes;

    public GlobalSymbolTable() {
        classes = new HashMap<String, ClassType>();
    }

    // lookup for class name
    public Type Lookup(String id) {
        ClassType ret = classes.get(id);
        if (ret == null) {
            return new UnknownType();
        } else {
            return ret;
        }
    }

    public boolean Enter(String id, Type typ) {
        Type old = classes.put(id, (ClassType)typ);
        // returns false if there was a previous element with same name
        return old == null;
    }
}