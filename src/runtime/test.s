.data
Bar$$: .quad 0
.quad Bar$boop
.quad Bar$bar

Foo$$: .quad 0

.text
.globl asm_main
asm_main:
movq $16,%rax
movq %rax,%rdi
call put
ret

Bar$bar:
movq $1,%rax
movq %rax,%rdi
call put
movq $5,%rax
ret

Bar$boop:
movq $3,%rax
movq %rax,%rdi
call put
movq $3,%rax
ret

