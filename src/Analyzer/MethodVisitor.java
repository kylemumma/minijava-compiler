package Analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import AST.*;
import AST.Visitor.Visitor;
import Analyzer.*;

// fills out a global symbol table from a program ast node

public class MethodVisitor implements Visitor {

  
  GlobalSymbolTable gst;
  ClassType currClass;
  MethodType currMethod;
  String currIdentifierName; // if finding out type later
  String mainClass;
  boolean isParam;
  boolean good;


  public boolean activate(Program p, GlobalSymbolTable g) {
    gst = g;
    good = true;
    p.accept(this);
    return good;
  }

  private void cannotFindSymbol(int line_number, String name) {
    System.err.println(line_number + ": error: cannot find symbol: " + name);
    good = false;
  }

  private void duplicateError(int line_number, String name, String methodName) {
    System.err.println(line_number + ": error: variable " + name + " is already defined in method " + methodName);
    good = false;
  }

  private void duplicateMethodError(int line_number, String name, String className) {
    System.err.println(line_number + ": error: method " + name + " is already defined in class " + className);
    good = false;
  }

  private void doesNotSupportMainClassInstances(int line_number) {
    System.err.println(line_number + ": error: MiniJava does not support instances of the main class");
    good = false;
  }

  private void notOverrideCorrectly(int line_number, String method) {
    System.err.println(line_number + ": error: The " + method + " method is not overridden correctly");
    good = false;
  }

  // for checking param list and ret type of methods
  private boolean exactlyMatches(Type t1, Type t2) {
    if (t1 instanceof ClassType && t2 instanceof ClassType) {
      return ((ClassType)t1).name == ((ClassType)t2).name;
    } else if (t1 instanceof BaseType && t2 instanceof BaseType) {
      return ((BaseType)t1).tp == ((BaseType)t2).tp;
    } else if (t1 instanceof VoidType && t2 instanceof VoidType) {
      return true;
    } else {
      return false;
    }
  }


  // MainClass m;
  // ClassDeclList cl;
  public void visit(Program n) {
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }
  }
  
  // Identifier i1,i2;
  // Statement s;
  public void visit(MainClass n) {
    // no need for symbol table for MainClass
    mainClass = n.i1.s;
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    currClass = (ClassType) gst.Lookup(n.i.s);
    for (int i = 0; i < n.ml.size(); i++) {
        n.ml.get(i).accept(this);
    }
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
    currClass = (ClassType) gst.Lookup(n.i.s);
    for (int i = 0; i < n.ml.size(); i++) {
        n.ml.get(i).accept(this);
    }
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    currIdentifierName = n.i.s;
    if (currIdentifierName == mainClass) {
        doesNotSupportMainClassInstances(n.line_number);
        return;
    }
    n.t.accept(this);
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) {
    // make method type and regular symbol table
    currMethod = new MethodType();
    currMethod.name = n.i.s;
    currMethod.params = new ArrayList<Type>();
    currMethod.st = new RegularSymbolTable(currClass.st.fields);
    isParam = true;
    for (int i = 0; i < n.fl.size(); i++) {
        n.fl.get(i).accept(this);
    }
    isParam = false;
    for (int i = 0; i < n.vl.size(); i++) {
        n.vl.get(i).accept(this);
    }
    currIdentifierName = null;
    n.t.accept(this);
    // don't bother with statement list or return expression in this visitor
    Type t = currClass.st.LookupHere(n.i.s);
    if (!(t instanceof UnknownType)) {
      // method declared in this class already!
      duplicateMethodError(n.line_number, n.i.s,  currClass.name);
      return;
    }
    t = currClass.st.Lookup(n.i.s);
    if (!(t instanceof UnknownType)) {
      // need to check this correctly overrides parent type
      MethodType og = (MethodType) t;
      if (og.params.size() != currMethod.params.size()) {
        notOverrideCorrectly(n.line_number, currMethod.name);
        return;
      } else if (!exactlyMatches(og.retType, currMethod.retType)) {
        notOverrideCorrectly(n.line_number, currMethod.name);
        return;
      } else {
        for (int i = 0; i < og.params.size(); i++) {
          if (!exactlyMatches(currMethod.params.get(i), og.params.get(i))) {
            notOverrideCorrectly(n.line_number, currMethod.name);
            return;
          }
        }
      }
    }
    currClass.st.Enter(n.i.s, currMethod);
    
  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {
    currIdentifierName = n.i.s;
    if (currIdentifierName == mainClass) {
        doesNotSupportMainClassInstances(n.line_number);
        return;
    }
    n.t.accept(this);
  }

  public void visit(IntArrayType n) {
    BaseType bt = new BaseType();
    bt.tp = type.INT_ARRAY;
    if (currIdentifierName == null) {
        // must be return type of method
        currMethod.retType = bt;
        return;
    }
    if (isParam) currMethod.params.add(bt);
    boolean res = currMethod.st.Enter(currIdentifierName, bt);
    if (!res) {
        duplicateError(n.line_number, currIdentifierName, currMethod.name);
    }
  }

  public void visit(BooleanType n) {
    BaseType bt = new BaseType();
    bt.tp = type.BOOLEAN;
    if (currIdentifierName == null) {
        // must be return type of method
        currMethod.retType = bt;
        return;
    }
    if (isParam) currMethod.params.add(bt);
    boolean res = currMethod.st.Enter(currIdentifierName, bt);
    if (!res) {
        duplicateError(n.line_number, currIdentifierName, currMethod.name);
    }

  }

  public void visit(IntegerType n) {
    BaseType bt = new BaseType();
    bt.tp = type.INT;
    if (currIdentifierName == null) {
        // must be return type of method
        currMethod.retType = bt;
        return;
    }
    if (isParam) currMethod.params.add(bt);
    boolean res = currMethod.st.Enter(currIdentifierName, bt);
    if (!res) {
        duplicateError(n.line_number, currIdentifierName, currMethod.name);
    }
  }

  // String s;
  public void visit(IdentifierType n) {
    Type t = gst.Lookup(n.s);
    if (t instanceof UnknownType) {
      cannotFindSymbol(n.line_number, n.s);
    }
    if (currIdentifierName == null) {
        // must be return type of method
        currMethod.retType = t;
        return;
    }
    if (isParam) currMethod.params.add(t);
    boolean res = currMethod.st.Enter(currIdentifierName, t);
    if (!res) {
        duplicateError(n.line_number, currIdentifierName, currMethod.name);
    }
  }

  // StatementList sl;
  public void visit(Block n) {

  }

  // Exp e;
  // Statement s1,s2;
  public void visit(If n) {

  }

  // Exp e;
  // Statement s;
  public void visit(While n) {

  }

  // Exp e;
  public void visit(Print n) {

  }
  
  // Identifier i;
  // Exp e;
  public void visit(Assign n) {

  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {

  }

  // Exp e1,e2;
  public void visit(And n) {

  }

  // Exp e1,e2;
  public void visit(LessThan n) {

  }

  // Exp e1,e2;
  public void visit(Plus n) {

  }

  // Exp e1,e2;
  public void visit(Minus n) {

  }

  // Exp e1,e2;
  public void visit(Times n) {

  }

  // Exp e1,e2;
  public void visit(ArrayLookup n) {

  }

  // Exp e;
  public void visit(ArrayLength n) {

  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {

  }

  // int i;
  public void visit(IntegerLiteral n) {

  }

  public void visit(True n) {

  }

  public void visit(False n) {

  }

  // String s;
  public void visit(IdentifierExp n) {

  }

  public void visit(This n) {

  }

  // Exp e;
  public void visit(NewArray n) {

  }

  // Identifier i;
  public void visit(NewObject n) {

  }

  // Exp e;
  public void visit(Not n) {

  }

  // String s;
  public void visit(Identifier n) {

  }
}
