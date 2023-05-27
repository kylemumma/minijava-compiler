package Compiler;

import AST.Visitor.Visitor;
import AST.*;
import Analyzer.Type.Type;
import Analyzer.Type.type;
import Analyzer.Type.UnknownType;
import Analyzer.SymbolTable.ClassSymbolTable;
import Analyzer.SymbolTable.GlobalSymbolTable;
import Analyzer.SymbolTable.RegularSymbolTable;
import Analyzer.Type.BaseType;
import Analyzer.Type.ClassType;
import Analyzer.Type.MethodType;

// fills out a global symbol table from a program ast node

public class CodegenVisitor implements Visitor {

  
  GlobalSymbolTable gst;
  boolean good;

  public boolean activate(Program p, GlobalSymbolTable g) {
    gst = g;
    good = true;
    p.accept(this);

    return good;
  }


  // write "op src,dst" to .asm output
  private void genbin(String op, String src, String dst) {
    System.out.println(op + " " + src + "," + dst);
  }

  // write "op lbl" to .asm output
  private void genbin(String op, String lbl) {
    System.out.println(op + " " + lbl);
  }

   // write "op lbl" to .asm output
   private void p(String str) {
    System.out.println(str);
  }

  private void duplicateError(int line_number, String name) {
    System.err.println(line_number + ": error: duplicate class: " + name);
    good = false;
  }

  // MainClass m;
  // ClassDeclList cl;
  public void visit(Program n) {
    p(".text");
    p(".globl asm_main");
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }
  }
  
  // Identifier i1,i2;
  // Statement s;
  public void visit(MainClass n) {
    p("asm_main:");
    n.s.accept(this);
    p("ret");
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    for (int i = 0; i < n.ml.size(); i++) {
        n.ml.get(i).accept(this);
    }
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
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
    for (int i = 0; i < n.sl.size(); i++) {
        n.sl.get(i).accept(this);
    }
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
    n.e.accept(this);
    p("movq %rax,%rdi");
    p("call put");
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
    n.e1.accept(this);
    p("pushq %rax");
    n.e2.accept(this);
    p("popq %rdx");
    p("addq %rdx,%rax");
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
    genbin("movq", "$" + Integer.toString(n.i), "%rax");
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
