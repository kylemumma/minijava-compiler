Basic$$: .quad 0

YoMama$$: .quad 0
.quad YoMama$aa
.quad YoMama$potato

.text
.globl asm_main
asm_main:
movq $16,%rdi
call mjcalloc
leaq YoMama$$(%rip),%rdx
movq %rdx,(%rax)
movq $-1,%rbx
pushq %rbx
movq (%rax),%rbx
pushq %rbx
call *16(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
ret

YoMama$potato:
pushq %rbp
movq %rsp, %rbp
subq $16,%rsp
movq $2,%rax
movq %rax,-8(%rbp)
movq 16(%rbp),%rax
movq $-1,%rbx
pushq %rbx
movq %rax,%rbx
pushq %rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
movq $16,%rdi
call mjcalloc
leaq YoMama$$(%rip),%rdx
movq %rdx,(%rax)
movq %rax,-8(%rbp)
movq -8(%rbp),%rax
movq $-1,%rbx
pushq %rbx
movq (%rax),%rbx
pushq %rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
movq -8(%rbp),%rax
movq $-1,%rbx
pushq %rbx
movq (%rax),%rbx
pushq %rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
movq 16(%rbp),%rax
movq $-1,%rbx
pushq %rbx
movq %rax,%rbx
pushq %rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
movq $16,%rdi
call mjcalloc
leaq YoMama$$(%rip),%rdx
movq %rdx,(%rax)
movq $-1,%rbx
pushq %rbx
movq (%rax),%rbx
pushq %rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
movq $5,%rax
addq $16,%rsp
popq %rbp
ret

YoMama$aa:
pushq %rbp
movq %rsp, %rbp
subq $0,%rsp
Unexpected internal compiler error: java.lang.ClassCastException: class Analyzer.Type.BaseType cannot be cast to class Analyzer.Type.ClassType (Analyzer.Type.BaseType and Analyzer.Type.ClassType are in unnamed module of loader 'app')
java.lang.ClassCastException: class Analyzer.Type.BaseType cannot be cast to class Analyzer.Type.ClassType (Analyzer.Type.BaseType and Analyzer.Type.ClassType are in unnamed module of loader 'app')
	at Compiler.CodegenVisitor.visit(CodegenVisitor.java:349)
	at AST.IdentifierExp.accept(IdentifierExp.java:14)
	at Compiler.CodegenVisitor.visit(CodegenVisitor.java:119)
	at AST.MethodDecl.accept(MethodDecl.java:21)
	at Compiler.CodegenVisitor.visit(CodegenVisitor.java:81)
	at AST.ClassDeclSimple.accept(ClassDeclSimple.java:18)
	at Compiler.CodegenVisitor.visit(CodegenVisitor.java:60)
	at AST.Program.accept(Program.java:16)
	at Compiler.CodegenVisitor.activate(CodegenVisitor.java:29)
	at Compiler.compiler.compile(compiler.java:30)
	at MiniJava.compile(MiniJava.java:149)
	at MiniJava.main(MiniJava.java:19)
