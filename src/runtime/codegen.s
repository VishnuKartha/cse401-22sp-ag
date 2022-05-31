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
	pushq	%rax
	pushq	%rdi
	addq	$1,%rax
	imulq	$8,%rax
	subq	$8,%rsp
	movq	%rax,%rdi
	call	_mjcalloc
	addq	$8,%rsp
	popq	%rdi
	popq	%rdx
	movq	%rdx,(%rax)
	movq	%rax,-8(%rbp)
	movq	$0,%rax
	pushq	%rax
	movq	$1,%rax
	popq	%rcx
	cmpq	0,%rcx
	jl	OutofBounds01
	movq	-8(%rbp),%rdx
	cmpq	%rcx,(%rdx)
	jle	OutofBounds01
	jmp	InBounds01
OutofBounds01:
	call	_mjerror
InBounds01:
	movq	%rax,8(%rdx,%rcx,8)
endArrayLookUp01:
	movq	$3,%rax
	movq	%rbp,%rsp
	popq	%rbp
	ret
		.data
Factorial$$:	.quad 0

One$$:	.quad 0
		.quad One$test
