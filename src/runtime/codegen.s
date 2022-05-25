	.text
	.globl _asm_main

_asm_main:
	pushq	%rbp
	movq	%rsp,%rbp

	movq	$8,%rdi
	call	_mjcalloc
	leaq	Fac$$(%rip),%rdx
	movq	%rdx,0(%rax)
	movq	%rax,%rdi
	movq	0(%rdi),%rax
	call	*8(%rax)

	movq	%rax,%rdi
	call	_put

	movq	%rbp,%rsp
	popq	%rbp
	ret

Fac$sumOf:
	pushq	%rbp
	movq	%rsp,%rbp
	subq	16,%rsp

	movq	%rbp,%rsp
	popq	%rbp
	ret
		.data
Factorial$$:	.quad 0

Fac$$:	.quad 0
		.quad Fac$sumOf
