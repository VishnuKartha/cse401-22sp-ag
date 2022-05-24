	.text
	.globl _asm_main

_asm_main:
	pushq	%rbp
	movq	%rsp,%rbp

	movq	%rax,%rdi
	call	_put

	movq	%rbp,%rsp
	popq	%rbp
	ret

Fac$ComputeFac:
	pushq	%rbp
	movq	%rsp,%rbp
	movq	$3,%rax
	pushq	%rax
	movq	$4,%rax
	popq	%rdx
	addq	%rdx,%rax

	movq	%rbp,%rsp
	popq	%rbp
	ret
		.data
Factorial$$:	.quad 0

Fac$$:	.quad 0
		.quad Fac$ComputeFac
