package Analyzer;

import java.util.Set;

enum type {
    INT,
    BOOLEAN,
    INT_ARRAY
}

public abstract class Type {
    // returns true if this type can be assigned to t
    // meaning t has to be equal to this or is something
    // that directly or indirectly extends this.
    abstract boolean assignmentCompatible(Type t);
}

class BaseType extends Type {
    type tp;
    boolean assignmentCompatible(Type t) {
        if (!(t instanceof BaseType)) {
            return false;
        }
        return tp == ((BaseType)t).tp;
    }
}

class ClassType extends Type {
    String name;
    Set<String> parents; // parent class names
    SymbolTable st;
    boolean assignmentCompatible(Type t) {
        if (!(t instanceof ClassType)) {
            return false;
        }
        return ((ClassType)t).parents.contains(name);
    }
}

class MethodType extends Type {
    String name;
    SymbolTable st;
    boolean assignmentCompatible(Type t) {
        // methods should never be assigned to anything
        return false;
    }
}

class VoidType extends Type {
    boolean assignmentCompatible(Type t) {
        // assigning to a rvalue/non-location
        return false;
    }
}

class UnknownType extends Type {
    boolean assignmentCompatible(Type t) {
        // assigning to undeclared identifier
        return false;
    }
}
