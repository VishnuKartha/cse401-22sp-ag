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
	movq %rbp,%rsp
	popq %rbp
	ret 
One$test:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
	.data
Factorial$$: .quad 0
One$$: .quad 0
		.quad One$test
