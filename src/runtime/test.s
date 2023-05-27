.text
.globl asm_main
asm_main:
movq $2,%rax
pushq %rax
movq $3,%rax
popq %rdx
imulq %rdx,%rax
pushq %rax
movq $5,%rax
popq %rdx
subq %rax,%rdx
movq %rdx,%rax
pushq %rax
movq $2,%rax
popq %rdx
addq %rdx,%rax
movq %rax,%rdi
call put
ret
