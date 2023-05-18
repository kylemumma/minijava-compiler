package Analyzer;

import java.util.List;
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

    BaseType () {}
    BaseType (type tp) {
        this.tp = tp;
    }

    boolean assignmentCompatible(Type t) {
        if (!(t instanceof BaseType)) {
            return false;
        }
        return tp == ((BaseType)t).tp;
    }
    @Override
    public String toString() {
        switch(tp) {
            case INT:
              return "INT";
            case BOOLEAN:
              return "BOOLEAN";
            case INT_ARRAY:
              return "INT_ARRAY";
            default:
              return "UNKNOWN";
        }
    }
}

class ClassType extends Type {
    String name;
    Set<String> parents; // parent class names
    ClassSymbolTable st;
    boolean assignmentCompatible(Type t) {
        if (!(t instanceof ClassType)) {
            return false;
        }
        ClassType ct = (ClassType)t;
        return (ct.name == name || ct.parents.contains(name));
    }
    @Override
    public String toString() {
        return name;
    }
}

class MethodType extends Type {
    String name;
    List<Type> params;
    Type retType;
    RegularSymbolTable st;
    boolean assignmentCompatible(Type t) {
        // methods should never be assigned to anything
        return false;
    }
    @Override
    public String toString() {
        return name;
    }
}

class VoidType extends Type {
    boolean assignmentCompatible(Type t) {
        // assigning to a rvalue/non-location
        return false;
    }

    @Override
    public String toString() {
        return "VOID";
    }
}

class UnknownType extends Type {
    boolean assignmentCompatible(Type t) {
        // assigning to undeclared identifier
        return false;
    }
}
