package Analyzer.Type;

import java.util.Set;

import Analyzer.SymbolTable.ClassSymbolTable;

public class ClassType extends Type {
    public String name;
    public Set<String> parents; // parent class names
    public ClassSymbolTable st;
    public boolean assignmentCompatible(Type t) {
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