	.text
	.globl _asm_main
_asm_main:
	pushq %rbp
	movq %rsp,%rbp
	pushq %rdi
	movq %rdi,%rbx
	pushq %rdi
	movq $8,%rdi
	call _mjcalloc
	popq %rdi
	leaq One$$(%rip),%rdx
	movq %rdx,0(%rax)
	movq %rax,%rdi
	pushq %rax
	movq (%rdi),%rax
	addq $8,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call _put
	popq %rdx
	popq %rdi
	movq $0,%rdi
	call _put
	movq %rbp,%rsp
	popq %rbp
	ret 
One$test:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq $5,%rax
	pushq %rdi
	pushq %rax
	addq $1,%rax
	imulq $8,%rax
	movq %rax,%rdi
	call _mjcalloc
	popq %rdx
	popq %rdi
	movq %rdx,0(%rax)
	movq %rax,-8(%rbp)
	movq $0,%rax
	pushq %rax
	movq $1,%rax
	popq %rdx
	movq -8(%rbp),%rcx
	cmpq %rdx,0(%rcx)
	jng arrayIndexOutOfBounds1
	cmpq $0,%rdx
	jl arrayIndexOutOfBounds1
	movq %rax,8(%rcx,%rdx,8)
	jmp done1
arrayIndexOutOfBounds1:
	call _mjerror
done1:
	movq $0,%rax
	pushq %rax
	movq -8(%rbp),%rax
	popq %rdx
	cmpq %rdx,0(%rax)
	jng arrayIndexOutOfBounds2
	cmpq $0,%rdx
	jl arrayIndexOutOfBounds2
	movq 8(%rax,%rdx,8),%rax
	jmp done2
arrayIndexOutOfBounds2:
	call _mjerror
done2:
	movq %rax,-16(%rbp)
	movq -16(%rbp),%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
	.data
Factorial$$: .quad 0
One$$: .quad 0
	 .quad One$test
