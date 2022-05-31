	.text
	.globl _asm_main

_asm_main:
	pushq	%rbp
	movq	%rsp,%rbp

	movq	$8,%rdi
	call	_mjcalloc
	leaq	One$$(%rip),%rdx
	movq	%rdx,0(%rax)
	movq	%rax,%rdi
	movq	0(%rdi),%rax
	call	*8(%rax)

	movq	%rax,%rdi
	call	_put
	movq	%rbp,%rsp
	popq	%rbp
	ret

One$test:
	pushq	%rbp
	movq	%rsp,%rbp
	subq	$8,%rsp
	movq	$5,%rax
	pushq	%rdi
	addq	$1,%rax
	imulq	$8,%rax
	movq	%rax,%rdi
	pushq	%rax
	subq	$8,%rsp
	call	_mjcalloc
	popq	%rdx
	popq	%rdx
	popq	%rdi
	movq	%rdx,0(%rax)
	movq	%rax,-8(%rbp)
	movq	$0,%rax
	pushq	%rax
	movq	$1,%rax
	popq	%rdx
	cmpq	0,%rcx
	jl	OutofBounds01
	movq	-8(%rbp),%rcx
	cmpq	%rdx,0(%rcx)
	jle	OutofBounds01
	movq	%rax,8(%rcx,%rdx,8)
	jmp	InBounds01
OutofBounds01:
	call	_mjerror
InBounds01:
endArrayLookUp01:
	movq	$6,%rax
	movq	%rbp,%rsp
	popq	%rbp
	ret
		.data
Factorial$$:	.quad 0

One$$:	.quad 0
		.quad One$test
