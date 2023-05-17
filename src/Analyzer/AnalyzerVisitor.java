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

public class AnalyzerVisitor implements Visitor {

  
  GlobalSymbolTable gst;
  Set<String> rootClasses; // no parents
  Map<String, List<String>> parentToChildren;
  List<ClassDeclSimple> simples;
  List<ClassDeclExtends> extenders;

  // for filling out parents list of a class
  private void dfs(String c) {
    ClassType curType = (ClassType)gst.Lookup(c);
    for (String child : parentToChildren.getOrDefault(c, new ArrayList<String>())) {
      ClassType t = (ClassType)gst.Lookup(child);
      t.parents.addAll(curType.parents);
      t.parents.add(c);
      dfs(child);
    }
  }

  public GlobalSymbolTable generateSymbolTable(Program p) {
    gst = new GlobalSymbolTable();
    rootClasses = new HashSet<String>();
    parentToChildren = new HashMap<String, List<String>>();
    simples = new ArrayList<ClassDeclSimple>();
    extenders = new ArrayList<ClassDeclExtends>();
    p.accept(this);
    // set the parents for all classes
    for (String root : rootClasses) {
      dfs(root);
    }

    // check for cyclic dependencies (only need to check things which have extends)
    for (ClassDeclExtends extender : extenders) {
      String name = extender.i.s;
      if (parentToChildren.get(name).contains(name)) {
        // extends itself directly or indirectly
        cycleError(extender.line_number, name);
      }
    }

    // build class symbol tables
    // mainclass 


    return gst;
  }


  private void duplicateError(int line_number, String name) {
    System.err.println(line_number + ": error: duplicate class: " + name);
  }

  private void cycleError(int line_number, String name) {
    System.err.println(line_number + ": error: cyclic inheritance involving " + name);
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
    ClassType t = new ClassType();
    t.name = n.i1.s;
    t.parents = new HashSet<>();
    t.st = new ClassSymbolTable();

    boolean res = gst.Enter(t.name, t);
    if (!res) {
      // a class with the same name has already been defined
      duplicateError(n.line_number, t.name);
    } else {
      rootClasses.add(t.name);
    }
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    ClassType t = new ClassType();
    t.name = n.i.s;
    t.parents = new HashSet<>();
    t.st = new ClassSymbolTable();

    boolean res = gst.Enter(t.name, t);
    if (!res) {
      // a class with the same name has already been defined
      duplicateError(n.line_number, t.name);
    } else {
      rootClasses.add(t.name);
      simples.add(n);
    }
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
    ClassType t = new ClassType();
    t.name = n.i.s;
    t.parents = new HashSet<>();
    t.st = new ClassSymbolTable();

    boolean res = gst.Enter(t.name, t);
    if (!res) {
      // a class with the same name has already been defined
      duplicateError(n.line_number, t.name);
    } else {
      parentToChildren.getOrDefault(n.j.s, new ArrayList<String>()).add(n.i.s);
      extenders.add(n);
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
 
  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {

  }

  public void visit(IntArrayType n) {

  }

  public void visit(BooleanType n) {
 
  }

  public void visit(IntegerType n) {

  }

  // String s;
  public void visit(IdentifierType n) {

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
