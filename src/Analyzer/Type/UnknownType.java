package Analyzer.Type;

public class UnknownType extends Type {
    public boolean assignmentCompatible(Type t) {
        // assigning to undeclared identifier
        return false;
    }

    @Override
    public String toString() {
        return "UNKNOWN";
    }
}