package Analyzer.Type;

public class VoidType extends Type {
    public boolean assignmentCompatible(Type t) {
        // assigning to a rvalue/non-location
        return false;
    }

    @Override
    public String toString() {
        return "VOID";
    }
}