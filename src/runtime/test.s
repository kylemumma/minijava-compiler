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
pushq %rax
movq $1313,%rax
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
movq %rax,%rdi
call put
movq $5,%rax
ret

