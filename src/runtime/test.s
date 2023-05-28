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
movq $-1,%rbx
pushq %rbx
movq (%rax),%rbx
pushq %rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
ret

YoMama$potato:
pushq %rbp
movq %rsp, %rbp
subq $8,%rsp
movq $1,%rax
movq %rax,-8(%rbp)
movq $0, %rax
movq %rax,-8(%rbp)
movq $5,%rax
movq %rax,%rdi
movq $2,%rax
cmpq %rdx, %rax
jg lessthan0
movq $0,%rax
jmp lessthan1
lessthan0:
movq $1, %rax
lessthan1:
movq %rax,-8(%rbp)
movq $5,%rax
addq $8,%rsp
popq %rbp
ret

