package AST.Visitor;

import AST.*;

// Sample print visitor from MiniJava web site with small modifications for UW CSE.
// HP 10/11

public class ASTPrintVisitor implements Visitor {

  private int indent = 0;
  private final static int indentSize = 2;
  private boolean nl = true;

  private void applyIndent() {
    if (!nl) return;
    for (int i = 0; i < indent * indentSize; i++) {
      System.out.print(" ");
    }
  }
  private void print(String s) {  
    applyIndent();
    System.out.print(s);
    nl = false;
  }

  private void println(String s) {
    applyIndent();
    System.out.println(s);
    nl = true;
  }

  // Display added for toy example language.  Not used in regular MiniJava
  public void visit(Display n) {
    println("Display");
    indent++;
    n.e.accept(this);
    indent--;
  }
  
  // MainClass m;
  // ClassDeclList cl;
  public void visit(Program n) {
    println("Program");
    indent++;
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }
    indent--;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public void visit(MainClass n) {
    print("MainClass ");
    n.i1.accept(this);
    println(" (line " + n.line_number + ")");
    indent++;
    n.s.accept(this);
    indent--;
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    print("Class ");
    n.i.accept(this);
    println(" (line " + n.line_number + ")");
    indent++;
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.get(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.get(i).accept(this);
    }
    indent--;
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
    print("Class ");
    n.i.accept(this);
    print(" extends ");
    n.j.accept(this);
    println(" (line " + n.line_number + ")");
    indent++;
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.get(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.get(i).accept(this);
    }
    indent--;
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    print("VarDecl ");
    n.i.accept(this);
    println("");
    indent++;
    n.t.accept(this);
    indent--;
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) {
    print("MethodDecl ");
    n.i.accept(this);
    println("");
    indent++;
    print("returns ");
    n.t.accept(this);
    println("");
    for ( int i = 0; i < n.fl.size(); i++ ) {
        println("parameters: ");
        indent++;
        n.fl.get(i).accept(this);
        indent--;
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.get(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.get(i).accept(this);
    }
    print("Return ");
    n.e.accept(this);
    println("");
    indent--;
  }

  // Type t;
  // Identifier i;
  public void visit(Formal n) {
    n.t.accept(this);
    print(" ");
    n.i.accept(this);
    println("");
  }

  public void visit(IntArrayType n) {
    print("IntArrayType");
  }

  public void visit(BooleanType n) {
    print("BooleanType");
  }

  public void visit(IntegerType n) {
    print("IntegerType");
  }

  // String s;
  public void visit(IdentifierType n) {
    print("IdentifierType(" + n.s + ")");
  }

  // StatementList sl;
  public void visit(Block n) {
    println("Block");
    indent++;
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.get(i).accept(this);
    }
    indent--;
  }

  // Exp e;
  // Statement s1,s2;
  public void visit(If n) {
    println("If");
    indent++;
    n.e.accept(this);
    println("");
    println("Then");
    indent++;
    n.s1.accept(this);
    indent--;
    println("Else");
    indent++;
    n.s2.accept(this);
    indent--;
  }

  // Exp e;
  // Statement s;
  public void visit(While n) {
    println("While");
    indent++;
    n.e.accept(this);
    println("Then");
    indent++;
    n.s.accept(this);
    indent--;
    indent--;
  }

  // Exp e;
  public void visit(Print n) {
    println("Print");
    indent++;
    n.e.accept(this);
    println("");
    indent--;
  }
  
  // Identifier i;
  // Exp e;
  public void visit(Assign n) {
    println("Assign");
    indent++;
    n.i.accept(this);
    println("");
    n.e.accept(this);
    indent--;
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {
    println("ArrayAssign");
    indent++;
    n.i.accept(this);
    print("[");
    n.e1.accept(this);
    print("]");
    println("");
    n.e2.accept(this);
    indent--;
  }

  // Exp e1,e2;
  public void visit(And n) {
    println("And");
    indent++;
    n.e1.accept(this);
    println("");
    n.e2.accept(this);
    indent--;
  }

  // Exp e1,e2;
  public void visit(LessThan n) {
    println("LessThan");
    indent++;
    n.e1.accept(this);
    println("");
    n.e2.accept(this);
    indent--;
  }

  // Exp e1,e2;
  public void visit(Plus n) {
    println("Plus");
    indent++;
    n.e1.accept(this);
    println("");
    n.e2.accept(this);
    indent--;
  }

  // Exp e1,e2;
  public void visit(Minus n) {
    println("Minus");
    indent++;
    n.e1.accept(this);
    println("");
    n.e2.accept(this);
    indent--;
  }

  // Exp e1,e2;
  public void visit(Times n) {
    println("Times");
    indent++;
    n.e1.accept(this);
    println("");
    n.e2.accept(this);
    indent--;
  }

  // Exp e1,e2;
  public void visit(ArrayLookup n) {
    println("ArrayLookup");
    indent++;
    n.e1.accept(this);
    println("");
    n.e2.accept(this);
    indent--;
  }

  // Exp e;
  public void visit(ArrayLength n) {
    println("ArrayLength");
    indent++;
    n.e.accept(this);
    indent--;
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
    println("Call ");
    indent++;
    n.e.accept(this);
    println("");
    n.i.accept(this);
    println("");
    println("arguments: ");
    indent++;
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.get(i).accept(this);
        if ( i == n.el.size()-1) continue;
        println("");
    }
    indent--;
    indent--;
  }

  // int i;
  public void visit(IntegerLiteral n) {
    print("IntegerLiteral ");
    print(Integer.toString(n.i));
  }

  public void visit(True n) {
    print("True");
  }

  public void visit(False n) {
    print("False");
  }

  // String s;
  public void visit(IdentifierExp n) {
    print(n.s);
  }

  public void visit(This n) {
    print("This");
  }

  // Exp e;
  public void visit(NewArray n) {
    println("NewArray ");
    indent++;
    n.e.accept(this);
    indent--;
  }

  // Identifier i;
  public void visit(NewObject n) {
    print("NewObject ");
    print(n.i.s);
  }

  // Exp e;
  public void visit(Not n) {
    println("Not");
    indent++;
    n.e.accept(this);
    indent--;
  }

  // String s;
  public void visit(Identifier n) {
    print(n.s);
  }
}
