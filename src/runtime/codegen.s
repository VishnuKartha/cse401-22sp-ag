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
	leaq One$$,%rdx
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
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
	.data
Factorial$$: .quad 0
    .data
One$$: .quad 0
		.quad One$test
