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


  public void activate(Program p, GlobalSymbolTable g) {
    gst = g;
    cst = new ClassSymbolTable();
    scope = new RegularSymbolTable();
    p.accept(this);
  }

  private void cannotFindSymbol(int line_number, String name) {
    System.err.println(line_number + ": error: cannot find symbol: " + name);
  }

  private void incompatibleTypes(int line_number, Type t1, Type t2) {
    System.err.println(line_number + ": error: type " + t1 + " cannot be assigned to type " + t2);
  }




  private void matchesBool(int line_number) {
    BaseType expectedType = new BaseType();
    expectedType.tp = type.BOOLEAN;
    if (!currType.assignmentCompatible(expectedType)) {
        incompatibleTypes(line_number, currType, expectedType);
    }
  }

  private void matchesInt(int line_number) {
    BaseType expectedType = new BaseType();
    expectedType.tp = type.INT;
    if (!currType.assignmentCompatible(expectedType)) {
        incompatibleTypes(line_number, currType, expectedType);
    }
  }

  private void matchesIntArray(int line_number) {
    BaseType expectedType = new BaseType();
    expectedType.tp = type.INT_ARRAY;
    if (!currType.assignmentCompatible(expectedType)) {
        incompatibleTypes(line_number, currType, expectedType);
    }
  }

  private void matches(Type t, int line_number) {
    if (!currType.assignmentCompatible(t)) {
        incompatibleTypes(line_number, currType, t);
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
    cst = ((ClassType)gst.Lookup(n.i1.s)).st;
    n.s.accept(this);
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    cst = ((ClassType)gst.Lookup(n.i.s)).st;
    for (int i = 0; i < n.ml.size(); i++) {
        n.ml.get(i).accept(this);
    }
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
    cst = ((ClassType)gst.Lookup(n.i.s)).st;
    for (int i = 0; i < n.ml.size(); i++) {
        n.ml.get(i).accept(this);
    }
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
    matches(m.retType, n.line_number);
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
    matchesBool(n.line_number);
    n.s1.accept(this);
    n.s2.accept(this);
    currType = null;
  }

  // Exp e;
  // Statement s;
  public void visit(While n) {
    n.e.accept(this);
    matchesBool(n.line_number);
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
    Type expectedType = currType;
    n.e.accept(this);
    matches(expectedType, n.line_number);
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {
    n.e1.accept(this);
    matchesInt(n.line_number);

    n.i.accept(this);
    matchesIntArray(n.line_number);

    n.e2.accept(this);
    matchesInt(n.line_number);
  }

  // Exp e1,e2;
  public void visit(And n) {
    n.e1.accept(this);
    matchesBool(n.line_number);

    n.e2.accept(this);
    matchesBool(n.line_number);
  }

  // Exp e1,e2;
  public void visit(LessThan n) {
    n.e1.accept(this);
    matchesInt(n.line_number);

    n.e2.accept(this);
    matchesInt(n.line_number);
  }

  // Exp e1,e2;
  public void visit(Plus n) {
    n.e1.accept(this);
    matchesInt(n.line_number);

    n.e2.accept(this);
    matchesInt(n.line_number);
  }

  // Exp e1,e2;
  public void visit(Minus n) {
    n.e1.accept(this);
    matchesInt(n.line_number);

    n.e2.accept(this);
    matchesInt(n.line_number);
  }

  // Exp e1,e2;
  public void visit(Times n) {
    n.e1.accept(this);
    matchesInt(n.line_number);

    n.e2.accept(this);
    matchesInt(n.line_number);
  }

  // Exp e1,e2;
  public void visit(ArrayLookup n) {
    n.e1.accept(this);
    matchesIntArray(n.line_number);

    n.e2.accept(this);
    matchesInt(n.line_number);
  }

  // Exp e;
  public void visit(ArrayLength n) {
    n.e.accept(this);
    matchesIntArray(n.line_number);
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
    n.e.accept(this);
    // have to do somethhing funny here teeheeeee AUfdawiufgafgawbufns
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
    Type t = scope.Lookup(n.s);
    if (t instanceof UnknownType) {
      cannotFindSymbol(n.line_number, n.s);
    }
    currType = t;
  }
}
