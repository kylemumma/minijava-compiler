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

public class StatementVisitor implements Visitor {

  
  GlobalSymbolTable gst;
  ClassSymbolTable cst;
  RegularSymbolTable scope;
  Type currType;
  String currClassName; // for handling This


  public void activate(Program p, GlobalSymbolTable g) {
    gst = g;
    cst = new ClassSymbolTable();
    scope = new RegularSymbolTable();
    p.accept(this);
  }

  private void cannotFindSymbol(int line_number, String name) {
    System.err.println(line_number + ": error: cannot find symbol: " + name);
  }

  private void incompatibleTypes(int line_number, Type t1, Type t2, String context) {
    System.err.println(line_number + ": error: type " + t1 + " cannot be assigned to type " + t2 + " in " + context);
  }

  private void expectedClass(int line_number) {
    System.err.println(line_number + ": error: expected class");
  }

  private void unknownMethod(int line_number, ClassType c, String symbol) {
    System.err.println(line_number + ": error: unknown method " + symbol + " in type " + c);
  }

  private void signatureNotMatch(int line_number, ClassType c, MethodType m) {
    System.err.println(line_number + ": error: method " + m + " signature does not match given arguments for class " + c);
  }




  private void matchesBool(int line_number, String context) {
    BaseType expectedType = new BaseType(type.BOOLEAN);
    if (!currType.assignmentCompatible(expectedType)) {
        incompatibleTypes(line_number, currType, expectedType, context);
    }
  }

  private void matchesInt(int line_number, String context) {
    BaseType expectedType = new BaseType(type.INT);
    if (!currType.assignmentCompatible(expectedType)) {
        incompatibleTypes(line_number, currType, expectedType, context);
    }
  }

  private void matchesIntArray(int line_number, String context) {
    BaseType expectedType = new BaseType(type.INT_ARRAY);
    if (!currType.assignmentCompatible(expectedType)) {
        incompatibleTypes(line_number, currType, expectedType, context);
    }
  }

  private void matches(Type a, Type b, int line_number, String context) {
    // is a = b valid?
    if (!a.assignmentCompatible(b)) {
        incompatibleTypes(line_number, a, b, context);
    }
  }



  // MainClass m;
  // ClassDeclList cl;
  public void visit(Program n) {
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }

    currType = null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public void visit(MainClass n) {
    // no need for symbol table for MainClass
    currClassName = n.i1.s;
    cst = ((ClassType)gst.Lookup(n.i1.s)).st;
    n.s.accept(this);

    currType = null;
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    currClassName = n.i.s;
    cst = ((ClassType)gst.Lookup(n.i.s)).st;
    for (int i = 0; i < n.ml.size(); i++) {
        n.ml.get(i).accept(this);
    }

    currType = null;
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
    currClassName = n.i.s;
    cst = ((ClassType)gst.Lookup(n.i.s)).st;
    for (int i = 0; i < n.ml.size(); i++) {
        n.ml.get(i).accept(this);
    }

    currType = null;
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) {
    // transition to method symbol table
    MethodType m = (MethodType)cst.Lookup(n.i.s);
    scope = m.st;
    for (int i = 0; i < n.sl.size(); i++) {
        n.sl.get(i).accept(this);
    }
    n.e.accept(this); // currType gets set
    matches(m.retType, currType, n.line_number, "return");

    currType = null;
  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {

  }

  public void visit(IntArrayType n) {
    BaseType bt = new BaseType();
    bt.tp = type.INT_ARRAY;
    currType = bt;
  }

  public void visit(BooleanType n) {
    BaseType bt = new BaseType();
    bt.tp = type.BOOLEAN;
    currType = bt;
  }

  public void visit(IntegerType n) {
    BaseType bt = new BaseType();
    bt.tp = type.INT;
    currType = bt;
  }

  // String s;
  public void visit(IdentifierType n) {
    Type t = gst.Lookup(n.s);
    if (t instanceof UnknownType) {
        cannotFindSymbol(n.line_number, n.s);
    }
    currType = t;
  }

  // StatementList sl;
  public void visit(Block n) {
    for (int i = 0; i < n.sl.size(); i++) {
        n.sl.get(i).accept(this);
    }
    currType = null;
  }

  // Exp e;
  // Statement s1,s2;
  public void visit(If n) {
    n.e.accept(this);
    matchesBool(n.line_number, "if");
    n.s1.accept(this);
    n.s2.accept(this);
    currType = null;
  }

  // Exp e;
  // Statement s;
  public void visit(While n) {
    n.e.accept(this);
    matchesBool(n.line_number, "while");
    n.s.accept(this);
    currType = null;
  }

  // Exp e;
  public void visit(Print n) {
    n.e.accept(this);
    currType = null;
  }
  
  // Identifier i;
  // Exp e;
  public void visit(Assign n) {
    n.i.accept(this);
    Type first = currType;
    n.e.accept(this);
    matches(first, currType, n.line_number, "assignment");

    currType = null;
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {
    n.e1.accept(this);
    matchesInt(n.line_number, "array index");

    n.i.accept(this);
    matchesIntArray(n.line_number, "int array assignment");

    n.e2.accept(this);
    matchesInt(n.line_number, "int array assignment");

    currType = null;
  }

  // Exp e1,e2;
  public void visit(And n) {
    n.e1.accept(this);
    matchesBool(n.line_number, "AND");

    n.e2.accept(this);
    matchesBool(n.line_number, "AND");

    currType = new BaseType(type.BOOLEAN);
  }

  // Exp e1,e2;
  public void visit(LessThan n) {
    n.e1.accept(this);
    matchesInt(n.line_number, "LESSTHAN");

    n.e2.accept(this);
    matchesInt(n.line_number, "LESSTHAN");

    currType = new BaseType(type.BOOLEAN);
  }

  // Exp e1,e2;
  public void visit(Plus n) {
    n.e1.accept(this);
    matchesInt(n.line_number, "PLUS");

    n.e2.accept(this);
    matchesInt(n.line_number, "PLUS");

    currType = new BaseType(type.INT);
  }

  // Exp e1,e2;
  public void visit(Minus n) {
    n.e1.accept(this);
    matchesInt(n.line_number, "MINUS");

    n.e2.accept(this);
    matchesInt(n.line_number, "MINUS");
    
    currType = new BaseType(type.INT);
  }

  // Exp e1,e2;
  public void visit(Times n) {
    n.e1.accept(this);
    matchesInt(n.line_number, "TIMES");

    n.e2.accept(this);
    matchesInt(n.line_number, "TIMES");
    
    currType = new BaseType(type.INT);
  }

  // Exp e1,e2;
  public void visit(ArrayLookup n) {
    n.e1.accept(this);
    matchesIntArray(n.line_number, "array lookup");

    n.e2.accept(this);
    matchesInt(n.line_number, "array index");

    currType = new BaseType(type.INT);
  }

  // Exp e;
  public void visit(ArrayLength n) {
    n.e.accept(this);
    matchesIntArray(n.line_number, "array length");

    currType = new BaseType(type.INT);
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
    n.e.accept(this);
    if (!(currType instanceof ClassType)) {
      expectedClass(n.line_number);
      return;
    }
    ClassType cl = (ClassType) currType;
    Type t = cl.st.Lookup(n.i.s);
    if (!(t instanceof MethodType)) {
      unknownMethod(n.line_number, cl, n.i.s);
      return;
    }
    MethodType m = (MethodType) t;
    for (int i = 0; i < m.params.size(); i++) {
      if (i >= n.el.size()) {
        signatureNotMatch(n.line_number, cl, m);
        break;
      }
      Type expectedType = m.params.get(i);
      n.el.get(i).accept(this);
      matches(expectedType, currType, n.line_number, "argument list");
    }
    currType = m.retType;
  }

  // int i;
  public void visit(IntegerLiteral n) {
    currType = new BaseType(type.INT);
  }

  public void visit(True n) {
    currType = new BaseType(type.BOOLEAN);
  }

  public void visit(False n) {
    currType = new BaseType(type.BOOLEAN);
  }

  // String s;
  public void visit(IdentifierExp n) {
    // can only be a variable i.e a = b (the b)
    Type t = scope.Lookup(n.s);
    if (t instanceof UnknownType) {
      cannotFindSymbol(n.line_number, n.s);
    }
    currType = t;
  }

  public void visit(This n) {
    currType = gst.Lookup(currClassName);
    if (!(currType instanceof ClassType)) {
      cannotFindSymbol(n.line_number, currClassName);
    }
  }

  // Exp e;
  public void visit(NewArray n) {
    n.e.accept(this);
    matchesInt(n.line_number, "array size");
    currType = new BaseType(type.INT_ARRAY);
  }

  // Identifier i;
  public void visit(NewObject n) {
    currType = gst.Lookup(n.i.s);
    if (!(currType instanceof ClassType)) {
      cannotFindSymbol(n.line_number, n.i.s);
    }
  }

  // Exp e;
  public void visit(Not n) {
    n.e.accept(this);
    matchesBool(n.line_number, "NOT");
    currType = new BaseType(type.BOOLEAN);
  }

  // String s;
  public void visit(Identifier n) {
    Type t = scope.Lookup(n.s);
    if (t instanceof UnknownType) {
      cannotFindSymbol(n.line_number, n.s);
    }
    currType = t;
  }
}
