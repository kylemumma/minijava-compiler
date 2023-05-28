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
  ClassSymbolTable currClass; // for call
  RegularSymbolTable scope;
  boolean good;
  int lblCounter;

  public boolean activate(Program p, GlobalSymbolTable g) {
    gst = g;
    good = true;
    lblCounter = 0;

    genVtables(g);

    p.accept(this);

    return good;
  }

  // doesnt work for extending
  private void genVtables(GlobalSymbolTable g) {
    if (!g.classes.isEmpty()) {
      p(".data");
    }
    for (String key : g.classes.keySet()) {
      ClassType curr = g.classes.get(key);
      if (curr.parents.isEmpty()) {
          p(curr.name + "$$: .quad 0");
      } else {
          // @todo confused bc of multiple parents
      }

      for (String k2 : curr.st.methods.keySet()) {
        p(".quad " + curr.name + "$" + curr.st.methods.get(k2).name);
      }
      p("");
    }
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

  private int id() {
    return lblCounter++;
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
    p("");
  }

  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    for (int i = 0; i < n.ml.size(); i++) {
        MethodDecl c = n.ml.get(i);
        p(n.i.s + "$" + c.i.s + ":");
        c.accept(this);
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
    p("pushq %rbp");
    p("movq %rsp, %rbp");
    genbin("subq", "$"+Integer.toString(8*n.vl.size()), "%rsp");

    scope = ((MethodType)currClass.Lookup(n.i.s)).st;
    for (int i = 0; i < n.sl.size(); i++) {
        n.sl.get(i).accept(this);
    }
    n.e.accept(this);

    genbin("addq", "$"+Integer.toString(8*n.vl.size()), "%rsp");
    p("popq %rbp");
    p("ret");
    p("");
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
    n.e.accept(this);
    Type t = scope.Lookup(n.i.s);
    genbin("movq", "rax", -t.offset+"(%rbp)");
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {

  }

  // Exp e1,e2;
  public void visit(And n) {
    String skipLabel = "and" + id();
    n.e1.accept(this);
    p("cmpq $0, %rax");
    p("je " + skipLabel);
    n.e2.accept(this);
    p(skipLabel + ":");
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
    n.e1.accept(this);
    p("pushq %rax");
    n.e2.accept(this);
    p("popq %rdx");
    p("subq %rax,%rdx");
    p("movq %rdx,%rax");
  }

  // Exp e1,e2;
  public void visit(Times n) {
    n.e1.accept(this);
    p("pushq %rax");
    n.e2.accept(this);
    p("popq %rdx");
    p("imulq %rdx,%rax");
  }

  // Exp e1,e2;
  public void visit(ArrayLookup n) {

  }

  // Exp e;
  public void visit(ArrayLength n) {

  }

  // Exp e; (new or this or identifierexp)
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
    n.e.accept(this);
                                // rax is now memory location of a class
    MethodType mt = (MethodType)currClass.Lookup(n.i.s);

    // push params
    int numParams = n.el.size() + 1;
    if (numParams % 2 == 1) {
      p("movq $-1,%rbx");
      p("pushq %rbx"); // junk memory
      numParams++;
    }
    p("movq (%rax),%rbx");
    p("pushq %rbx"); // rbx should not be changed in evaluating expressions
    for (int i = 0; i < n.el.size(); i++) {
      n.el.get(i).accept(this);
      p("pushq %rax");
    }

    p("call *" + mt.offset + "(%rbx)");

    // pop params
    for (int i = 0; i < numParams; i++) {
      p("popq %rdi");
    }
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
    Type t = scope.Lookup(n.s);
    p("movq " + -t.offset+"(%rbp)" + ",%rax");
  }

  public void visit(This n) {

  }

  // Exp e;
  public void visit(NewArray n) {

  }

  // Identifier i;
  public void visit(NewObject n) {
    // @todo will need to change num_bytes param once fields become a thing
    currClass = ((ClassType)gst.Lookup(n.i.s)).st;
    p("movq $8,%rdi");
    p("call mjcalloc");
    p("leaq "+ n.i.s + "$$(%rip),%rdx");  // %rip is used for pc-relative addressing....
    p("movq %rdx,(%rax)");
  }

  // Exp e;
  public void visit(Not n) {

  }

  // String s;
  public void visit(Identifier n) {

  }
}
