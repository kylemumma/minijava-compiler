.data
Basic$$: .quad 0

YoMama$$: .quad 0
.quad YoMama$potato

.text
.globl asm_main
asm_main:
movq $8,%rdi
call mjcalloc
leaq YoMama$$(%rip),%rdx
movq %rdx,(%rax)
movq (%rax),%rbx
pushq %rbx
movq $515315,%rax
pushq %rax
movq $42,%rax
pushq %rax
movq $2,%rax
pushq %rax
call *8(%rbx)
popq %rdi
popq %rdi
popq %rdi
popq %rdi
movq %rax,%rdi
call put
ret

YoMama$potato:
pushq %rbp
movq %rsp, %rbp
subq $8,%rsp
movq 32(%rbp),%rax
movq %rax,%rdi
call put
movq 24(%rbp),%rax
movq %rax,%rdi
call put
movq 16(%rbp),%rax
movq %rax,%rdi
call put
movq 32(%rbp),%rax
movq %rax,-8(%rbp)
movq -8(%rbp),%rax
addq $8,%rsp
popq %rbp
ret

