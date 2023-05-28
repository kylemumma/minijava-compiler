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
movq (%rax),%rax
pushq %rax
call *8(%rax)
popq %rdi
movq %rax,%rdi
call put
ret

YoMama$potato:
movq $0,%rax
ret

