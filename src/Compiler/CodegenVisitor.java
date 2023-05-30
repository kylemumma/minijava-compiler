package Compiler;

import AST.Visitor.Visitor;

import AST.*;
import Analyzer.Type.Type;
import Analyzer.Type.UnknownType;
import Analyzer.SymbolTable.ClassSymbolTable;
import Analyzer.SymbolTable.GlobalSymbolTable;
import Analyzer.Type.ClassType;
import Analyzer.Type.MethodType;

// fills out a global symbol table from a program ast node

public class CodegenVisitor implements Visitor {

  
  GlobalSymbolTable gst;
  ClassSymbolTable currClass; // for methods
  ClassSymbolTable callClass; // for call
  MethodType scope;
  boolean good;
  int lblCounter;

  public boolean activate(Program p, GlobalSymbolTable g) {
    gst = g;
    good = true;
    lblCounter = 0;

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

  // get pointer to curr classes memory address
  private void getThis() {
    int thisOffset = -scope.params.size() * 8 - 16; // each param + ret + rbp
    p("movq " + -thisOffset + "(%rbp)" + ",%rax");
  }

  private int id() {
    return lblCounter++;
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
    currClass = ((ClassType)gst.Lookup(n.i.s)).st;
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
    currClass = ((ClassType)gst.Lookup(n.i.s)).st;
    for (int i = 0; i < n.ml.size(); i++) {
        MethodDecl c = n.ml.get(i);
        p(n.i.s + "$" + c.i.s + ":");
        c.accept(this);
    }
  }

  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    // unnecessary
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

    scope = (MethodType)currClass.Lookup(n.i.s);
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
    // unneccessary
  }

  public void visit(IntArrayType n) {
    // unneccessary
  }

  public void visit(BooleanType n) {
    // unneccessary
  }

  public void visit(IntegerType n) {
    // unneccessary
  }

  // String s;
  public void visit(IdentifierType n) {
    // unneccessary
  }

  // StatementList sl;
  public void visit(Block n) {
    for (int i = 0; i < n.sl.size(); i++) {
      n.sl.get(i).accept(this);
    }
  }

  // Exp e;
  // Statement s1,s2;
  public void visit(If n) {
    String labelNum = Integer.toString(id());
    n.e.accept(this);
    p("cmpq $0, %rax");  // eval condition
    p("jz else" + labelNum);  // if false jmp to else
    n.s1.accept(this);  // true stmt
    p("jmp done" + labelNum);  // jmp done

    p("else" + labelNum + ":");  // else label???
    n.s2.accept(this);  // else stmt

    p("done" + labelNum + ":");  // done label
  }

  // Exp e;
  // Statement s;
  public void visit(While n) {
    String labelNum = Integer.toString(id());
    p("while" + labelNum + ":");
    n.e.accept(this);
    p("cmpq $0,%rax");
    p("jz done" + labelNum);
    n.s.accept(this);
    p("jmp while" + labelNum);
    p("done" + labelNum + ":");
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
    Type idtype = scope.st.LookupHere(n.i.s);
    boolean local = true;
    if (idtype instanceof UnknownType) {
      // is a field, not a local variable
      idtype = scope.st.Lookup(n.i.s);
      local = false;
    }
    
    if (local) {
      n.e.accept(this);
      genbin("movq", "%rax", -idtype.offset+"(%rbp)");
    } else {
      getThis();
      p("pushq %rax");
      n.e.accept(this);
      p("popq %rdi");
      genbin("movq", "%rax", idtype.offset+"(%rdi)");
    }
  }

  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {
    Type idtype = scope.st.LookupHere(n.i.s);
    boolean local = true;
    if (idtype instanceof UnknownType) {
      // is a field, not a local variable
      idtype = scope.st.Lookup(n.i.s);
      local = false;
    }
    
    if (local) {
      n.e1.accept(this);
      p("pushq %rax");
      n.e2.accept(this);
      p("popq %rdx");
      p("movq %rbp,%rdi");
      p("subq $" + idtype.offset + ",%rdi");
      p("movq (%rdi),%rdi");

      // bounds check
      p("movq %rdx,%rsi");
      p("pushq %rax");
      p("pushq %rdi");
      p("pushq %rdx");
      p("pushq %rdx");
      p("call check_arr_bounds");
      p("popq %rdx");
      p("popq %rdx");
      p("popq %rdi");
      p("popq %rax");

      p("movq %rax,(%rdi,%rdx,8)");
    } else {
      n.e1.accept(this);
      p("pushq %rax");
      getThis();
      p("pushq %rax");
      n.e2.accept(this);
      p("popq %rdi");
      p("popq %rdx");
      p("addq $" + idtype.offset + ",%rdi");
      p("movq (%rdi),%rdi");

      // bounds check
      p("movq %rdx,%rsi");
      p("pushq %rax");
      p("pushq %rdi");
      p("pushq %rdx");
      p("pushq %rdx");
      p("call check_arr_bounds");
      p("popq %rdx");
      p("popq %rdx");
      p("popq %rdi");
      p("popq %rax");

      p("movq %rax,(%rdi,%rdx,8)");
    }
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
    String uniqueId = Integer.toString(id());
    n.e1.accept(this);
    p("pushq %rax");
    n.e2.accept(this);
    p("cmpq %rax,(%rsp)");  // e1 - e2
    p("popq %rax");
    p("jl lessthan" + uniqueId);  // if e1-e2<0, then e1 < e2
    p("movq $0,%rax");  // e1 >= e2
    p("jmp ltdone" + uniqueId);

    p("lessthan" + uniqueId + ":");  // e1 < e2
    p("movq $1,%rax");

    p("ltdone" + uniqueId + ":");
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
    // bounds check
    n.e1.accept(this); 
    p("pushq %rax");
    p("pushq %rax");
    n.e2.accept(this);
    p("popq %rdx");
    p("movq %rdx,%rdi");
    p("movq %rax,%rsi");
    p("pushq %rax");
    p("call check_arr_bounds");
    p("popq %rax");
    p("popq %rdx");
    p("movq (%rdx,%rax,8),%rax");  // rax<-*(e1 + 8*e2)
  }

  // Exp e;
  public void visit(ArrayLength n) {
    n.e.accept(this);
    p("movq -8(%rax),%rax");
  }

  // Exp e; (new or this or identifierexp)
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
    n.e.accept(this);
                                // rax is now memory location of a class
    MethodType mt = (MethodType)callClass.Lookup(n.i.s);

    // push params
    int numParams = n.el.size() + 1;
    if (numParams % 2 == 1) {
      p("movq $-1,%rbx");
      p("pushq %rbx"); // junk memory
      numParams++;
    }
    // expect $rax holds the memory address of the instance
    p("pushq %rax");
    p("movq (%rax),%rbx");
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
    p("movq $1,%rax");
  }

  public void visit(False n) {
    p("movq $0, %rax");
  }

  // String s;
  public void visit(IdentifierExp n) {
    Type idtype = scope.st.LookupHere(n.s);
    boolean local = true;
    if (idtype instanceof UnknownType) {
      // is a field, not a local variable
      idtype = scope.st.Lookup(n.s);
      local = false;
    }
    if (idtype instanceof ClassType) {
      callClass = ((ClassType)idtype).st;
    } else {
      callClass = null;
    }
    if (local) {
      p("movq " + -idtype.offset+"(%rbp)" + ",%rax");
    } else {
      getThis();
      p("movq " + idtype.offset+"(%rax), %rax");
    }
    
  }

  public void visit(This n) {
    callClass = currClass;
    getThis();
  }

  // Exp e;
  public void visit(NewArray n) {
    n.e.accept(this);
    p("pushq %rax");  // store length for later
    p("addq $1,%rax");  // add extra spot for arr_length
    p("imulq $8,%rax");  
    p("movq %rax,%rdi");
    p("call mjcalloc");
    p("popq %rdx");
    p("movq %rdx,(%rax)");  // the quad before the array is its length
    p("addq $8, %rax");
  }

  // return mem addr
  // Identifier i;
  public void visit(NewObject n) {
    ClassType ct = (ClassType)gst.Lookup(n.i.s);
    callClass = ct.st;
    p("movq $" + ct.sz +",%rdi");
    p("call mjcalloc");
    // %rip is used for pc-relative addressing, the addr of our .data label is an offset from rip (pc)
    p("leaq "+ n.i.s + "$$(%rip),%rdx");
    p("movq %rdx,(%rax)");
  }

  // Exp e;
  public void visit(Not n) {
    n.e.accept(this);
    p("xorq $1,%rax");
  }

  // String s;
  public void visit(Identifier n) {
    // unnecesary
  }
}
