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

public class FieldVisitor implements Visitor {

  
  GlobalSymbolTable gst;
  ClassType currClass;
  String currIdentifierName; // if finding out type later
  String mainClass;
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

  private void duplicateError(int line_number, String name, String className) {
    System.err.println(line_number + ": error: variable " + name + " is already defined in class " + className);
    good = false;
  }

  private void doesNotSupportMainClassInstances(int line_number) {
    System.err.println(line_number + ": error: MiniJava does not support instances of the main class");
    good = false;
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
    for (int i = 0; i < n.vl.size(); i++) {
        n.vl.get(i).accept(this);
    }
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
    currClass = (ClassType) gst.Lookup(n.i.s);
    for (int i = 0; i < n.vl.size(); i++) {
        n.vl.get(i).accept(this);
    }
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    currIdentifierName = n.i.s;
    n.t.accept(this);
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) {

  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {

  }

  public void visit(IntArrayType n) {
    BaseType bt = new BaseType();
    bt.tp = type.INT_ARRAY;
    boolean res = currClass.st.fields.Enter(currIdentifierName, bt);
    if (!res) {
        duplicateError(n.line_number, currIdentifierName, currClass.name);
    }
  }

  public void visit(BooleanType n) {
    BaseType bt = new BaseType();
    bt.tp = type.BOOLEAN;
    boolean res = currClass.st.fields.Enter(currIdentifierName, bt);
    if (!res) {
        duplicateError(n.line_number, currIdentifierName, currClass.name);
    }

  }

  public void visit(IntegerType n) {
    BaseType bt = new BaseType();
    bt.tp = type.INT;
    boolean res = currClass.st.fields.Enter(currIdentifierName, bt);
    if (!res) {
        duplicateError(n.line_number, currIdentifierName, currClass.name);
    }
  }

  // String s;
  public void visit(IdentifierType n) {
    if (n.s.equals(mainClass)) {
      doesNotSupportMainClassInstances(n.line_number);
      return;
  }
    Type t = gst.Lookup(n.s);
    if (t instanceof UnknownType) {
        cannotFindSymbol(n.line_number, n.s);
    } else {
        ClassType ct = (ClassType)t;
        boolean res = currClass.st.fields.Enter(currIdentifierName, ct);
        if (!res) {
            duplicateError(n.line_number, currIdentifierName, currClass.name);
        }
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
