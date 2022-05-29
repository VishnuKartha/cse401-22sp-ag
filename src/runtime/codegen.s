	.text
	.globl _asm_main

_asm_main:
	pushq	%rbp
	movq	%rsp,%rbp

	movq	$24,%rdi
	call	_mjcalloc
	leaq	Fac$$(%rip),%rdx
	movq	%rdx,0(%rax)
	movq	%rax,%rdi
	movq	0(%rdi),%rax
	call	*24(%rax)

	movq	%rax,%rdi
	call	_put
	movq	%rbp,%rsp
	popq	%rbp
	ret

Fac$sumOf:
	pushq	%rbp
	movq	%rsp,%rbp
	subq	$0,%rsp
	movq	16(%rbp),%rax
	movq	%rax,8(%rdi)
	movq	24(%rbp),%rax
	movq	%rax,16(%rdi)
	movq	8(%rdi),%rax
	pushq	%rax
	movq	16(%rdi),%rax
	popq	%rdx
	addq	%rdx,%rax

	movq	%rbp,%rsp
	popq	%rbp
	ret
Fac$bruh:
	pushq	%rbp
	movq	%rsp,%rbp
	subq	$0,%rsp
	movq	$0,%rax
	movq	%rbp,%rsp
	popq	%rbp
	ret
Fac$hue:
	pushq	%rbp
	movq	%rsp,%rbp
	subq	$0,%rsp
	movq	$1,%rax
	movq	%rbp,%rsp
	popq	%rbp
	ret
		.data
Factorial$$:	.quad 0

Fac$$:	.quad 0
		.quad Fac$sumOf
		.quad Fac$bruh
		.quad Fac$hue
