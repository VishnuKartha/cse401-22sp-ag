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
	subq	$8,%rsp
	movq	$5,%rax
	pushq	%rax
	movq	$4,%rax
	pushq	%rax
	movq	$3,%rax
	pushq	%rax
	call	*8(%rax)

	movq	%rax,%rdi
	call	_put
	movq	%rbp,%rsp
	popq	%rbp
	ret

Fac$sumOf:
	pushq	%rbp
	movq	%rsp,%rbp
	subq	$24,%rsp
	movq	32(%rbp),%rax
	movq	%rax,-16(%rbp)
	movq	32(%rbp),%rax
	movq	%rax,-16(%rbp)
	movq	32(%rbp),%rax
	movq	%rax,-16(%rbp)
	movq	-16(%rbp),%rax
	pushq	%rax
	movq	-16(%rbp),%rax
	popq	%rdx
	addq	%rdx,%rax

	pushq	%rax
	movq	-16(%rbp),%rax
	popq	%rdx
	addq	%rdx,%rax

	movq	%rbp,%rsp
	popq	%rbp
	ret
		.data
Factorial$$:	.quad 0

Fac$$:	.quad 0
		.quad Fac$sumOf
