.text
.globl asm_main
asm_main:
movq $5,%rax
pushq %rax
movq $64,%rax
popq %rdx
addq %rdx,%rax
movq %rax,%rdi
call put
ret
