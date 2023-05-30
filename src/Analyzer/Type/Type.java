package Analyzer.Type;

public abstract class Type {
    // returns true if this type can be assigned to t
    // meaning t has to be equal to this or is something
    // that directly or indirectly extends this.
    public int offset = 99;
    public abstract boolean assignmentCompatible(Type t);
}
