package Analyzer.Type;

public class BaseType extends Type {
    public type tp;


    public BaseType () {}
    public BaseType (type tp) {
        this.tp = tp;
    }

    public boolean assignmentCompatible(Type t) {
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