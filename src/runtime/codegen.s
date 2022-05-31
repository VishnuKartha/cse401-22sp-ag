	.text
	.globl _asm_main
_asm_main:
	pushq %rbp
	movq %rsp,%rbp
	movq $3,%rax
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call _put
	popq %rdx
	popq %rdi
	movq %rbp,%rsp
	popq %rbp
	ret 
One$setTag:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
One$getTag:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
One$setIt:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
One$getIt:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
Two$setTag:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
Two$getThat:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
Two$resetIt:
	pushq %rbp
	movq %rsp,%rbp
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
	.data
Factorial$$: .quad 0
One$$: .quad 0
		.quad One$setTag
		.quad One$getTag
		.quad One$setIt
		.quad One$getIt
Two$$: .quad One$$
		.quad Two$setTag
		.quad One$getTag
		.quad One$setIt
		.quad One$getIt
		.quad Two$getThat
		.quad Two$resetIt
