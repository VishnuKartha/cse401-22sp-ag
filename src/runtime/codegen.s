	.text
	.globl _asm_main

_asm_main:
	pushq	%rbp
	movq	%rsp,%rbp

	movq	$57,%rax
	movq	%rax,%rdi
	call	_put

	movq	%rbp,%rsp
	popq	%rbp
	ret

