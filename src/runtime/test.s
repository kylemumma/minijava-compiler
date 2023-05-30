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
pushq %rax
movq (%rax),%rbx
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
movq 16(%rbp),%rax
movq %rax,%rdi
movq $2,%rax
movq %rax,8(%rdi)
movq 16(%rbp),%rax
movq $-1,%rbx
pushq %rbx
pushq %rax
movq (%rax),%rbx
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
pushq %rax
movq (%rax),%rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
movq -8(%rbp),%rax
movq $-1,%rbx
pushq %rbx
pushq %rax
movq (%rax),%rbx
call *8(%rbx)
popq %rdi
popq %rdi
movq %rax,%rdi
call put
movq 16(%rbp),%rax
movq $-1,%rbx
pushq %rbx
pushq %rax
movq (%rax),%rbx
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
pushq %rax
movq (%rax),%rbx
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
movq 16(%rbp),%rax
movq 8(%rax), %rax
addq $0,%rsp
popq %rbp
ret

