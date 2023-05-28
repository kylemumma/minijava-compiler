package Analyzer.Type;

import java.util.List;

import Analyzer.SymbolTable.RegularSymbolTable;

public class MethodType extends Type {
    public String name;
    public List<Type> params;
    public int sz;
    public Type retType;
    public RegularSymbolTable st;
    public boolean assignmentCompatible(Type t) {
        // methods should never be assigned to anything
        return false;
    }
    @Override
    public String toString() {
        return name;
    }
}