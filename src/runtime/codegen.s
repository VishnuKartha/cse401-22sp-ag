	.text
	.globl _asm_main
_asm_main:
	pushq %rbp
	movq %rsp,%rbp
	pushq %rdi
	movq %rdi,%rbx
	pushq %rdi
	movq $8,%rdi
	pushq %rax
	call _mjcalloc
	popq %rdx
	popq %rdi
	leaq One$$(%rip),%rdx
	movq %rdx,0(%rax)
	movq %rax,%rdi
	movq (%rdi),%rax
	addq $8,%rax
	call *(%rax)
	popq %rdi
	pushq %rdi
	movq %rax,%rdi
	call _put
	popq %rdi
	movq %rbp,%rsp
	popq %rbp
	ret 
One$test:
	pushq %rbp
	movq %rsp,%rbp
	subq $8,%rsp
	movq $5,%rax
	pushq %rdi
	pushq %rax
	addq $1,%rax
	imulq $8,%rax
	movq %rax,%rdi
	pushq %rax
	call _mjcalloc
	popq %rdx
	movq $0,%rdi
	call _put
	popq %rdx
	popq %rdi
	movq %rdx,0(%rax)
	movq %rax,-8(%rbp)
	movq $6,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
	.data
Factorial$$: .quad 0
One$$: .quad 0
	 .quad One$test
