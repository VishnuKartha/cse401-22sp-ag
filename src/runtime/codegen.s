	.text
	.globl asm_main
asm_main:
	pushq %rbp
	movq %rsp,%rbp
	pushq %rdi
	movq %rdi,%rbx
	pushq %rdi
	movq $24,%rdi
	call mjcalloc
	popq %rdi
	leaq LS$$(%rip),%rdx
	movq %rdx,0(%rax)
	movq %rax,%rdi
	movq $10,%rax
	pushq %rax
	movq (%rdi),%rax
	addq $8,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call put
	popq %rdx
	popq %rdi
	movq %rbp,%rsp
	popq %rbp
	ret 
LS$Start:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	pushq %rdi
	movq %rdi,%rbx
	movq %rdi,%rax
	movq %rax,%rdi
	movq 16(%rbp),%rax
	pushq %rax
	movq (%rdi),%rax
	addq $32,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	movq %rax,-8(%rbp)
	pushq %rdi
	movq %rdi,%rbx
	movq %rdi,%rax
	movq %rax,%rdi
	pushq %rax
	movq (%rdi),%rax
	addq $16,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	movq %rax,-16(%rbp)
	movq $9999,%rax
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call put
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rdi,%rbx
	movq %rdi,%rax
	movq %rax,%rdi
	movq $8,%rax
	pushq %rax
	movq (%rdi),%rax
	addq $24,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call put
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rdi,%rbx
	movq %rdi,%rax
	movq %rax,%rdi
	movq $12,%rax
	pushq %rax
	movq (%rdi),%rax
	addq $24,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call put
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rdi,%rbx
	movq %rdi,%rax
	movq %rax,%rdi
	movq $17,%rax
	pushq %rax
	movq (%rdi),%rax
	addq $24,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call put
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rdi,%rbx
	movq %rdi,%rax
	movq %rax,%rdi
	movq $50,%rax
	pushq %rax
	movq (%rdi),%rax
	addq $24,%rax
	call *(%rax)
	popq %rdx
	popq %rdi
	pushq %rdi
	movq %rax,%rdi
	pushq %rax
	call put
	popq %rdx
	popq %rdi
	movq $55,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
LS$Print:
	pushq %rbp
	movq %rsp,%rbp
	subq $8,%rsp
	movq $1,%rax
	movq %rax,-8(%rbp)
while1:
	movq -8(%rbp),%rax
	pushq %rax
	movq 16(%rdi),%rax
	popq %rdx
	cmpq %rdx,%rax
	jng lessThanFalse1
	movq $1,%rax
	jmp end1
lessThanFalse1:
	movq $0,%rax
end1:
	cmpq $1,%rax
	jne end2
	movq -8(%rbp),%rax
	pushq %rax
	movq 8(%rdi),%rax
	popq %rdx
	cmpq %rdx,0(%rax)
	jng arrayIndexOutOfBounds1
	cmpq $0,%rdx
	jl arrayIndexOutOfBounds1
	movq 8(%rax,%rdx,8),%rax
	jmp end3
arrayIndexOutOfBounds1:
	pushq %rax
	call mjerror
	popq %rdx
end3:
	pushq %rdi
	movq %rax,%rdi
	call put
	popq %rdi
	movq -8(%rbp),%rax
	pushq %rax
	movq $1,%rax
	popq %rdx
	addq %rdx,%rax
	movq %rax,-8(%rbp)
	jmp while1
end2:
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
LS$Search:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq $1,%rax
	movq %rax,-8(%rbp)
	movq $0,%rax
	movq %rax,-16(%rbp)
	movq $0,%rax
	movq %rax,-24(%rbp)
while2:
	movq -8(%rbp),%rax
	pushq %rax
	movq 16(%rdi),%rax
	popq %rdx
	cmpq %rdx,%rax
	jng lessThanFalse2
	movq $1,%rax
	jmp end4
lessThanFalse2:
	movq $0,%rax
end4:
	cmpq $1,%rax
	jne end5
	movq -8(%rbp),%rax
	pushq %rax
	movq 8(%rdi),%rax
	popq %rdx
	cmpq %rdx,0(%rax)
	jng arrayIndexOutOfBounds2
	cmpq $0,%rdx
	jl arrayIndexOutOfBounds2
	movq 8(%rax,%rdx,8),%rax
	jmp end6
arrayIndexOutOfBounds2:
	call mjerror
end6:
	movq %rax,-32(%rbp)
	movq 16(%rbp),%rax
	pushq %rax
	movq $1,%rax
	popq %rdx
	addq %rdx,%rax
	movq %rax,-40(%rbp)
	movq -32(%rbp),%rax
	pushq %rax
	movq 16(%rbp),%rax
	popq %rdx
	cmpq %rdx,%rax
	jng lessThanFalse3
	movq $1,%rax
	jmp end7
lessThanFalse3:
	movq $0,%rax
end7:
	cmpq $1,%rax
	jne else1
	movq $0,%rax
	movq %rax,-48(%rbp)
	jmp end8
else1:
	movq -32(%rbp),%rax
	pushq %rax
	movq -40(%rbp),%rax
	popq %rdx
	cmpq %rdx,%rax
	jng lessThanFalse4
	movq $1,%rax
	jmp end9
lessThanFalse4:
	movq $0,%rax
end9:
	cmpq $1,%rax
	je notFalse1
	movq $1,%rax
	jmp end10
notFalse1:
	movq $0,%rax
end10:
	cmpq $1,%rax
	jne else2
	movq $0,%rax
	movq %rax,-48(%rbp)
	jmp end11
else2:
	movq $1,%rax
	movq %rax,-16(%rbp)
	movq $1,%rax
	movq %rax,-24(%rbp)
	movq 16(%rdi),%rax
	movq %rax,-8(%rbp)
end11:
end8:
	movq -8(%rbp),%rax
	pushq %rax
	movq $1,%rax
	popq %rdx
	addq %rdx,%rax
	movq %rax,-8(%rbp)
	jmp while2
end5:
	movq -24(%rbp),%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
LS$Init:
	pushq %rbp
	movq %rsp,%rbp
	subq $32,%rsp
	movq 16(%rbp),%rax
	movq %rax,16(%rdi)
	movq 16(%rbp),%rax
	pushq %rdi
	pushq %rax
	addq $1,%rax
	imulq $8,%rax
	movq %rax,%rdi
	call mjcalloc
	popq %rdx
	popq %rdi
	movq %rdx,0(%rax)
	movq %rax,8(%rdi)
	movq $1,%rax
	movq %rax,-8(%rbp)
	movq 16(%rdi),%rax
	pushq %rax
	movq $1,%rax
	popq %rdx
	addq %rdx,%rax
	movq %rax,-16(%rbp)
while3:
	movq -8(%rbp),%rax
	pushq %rax
	movq 16(%rdi),%rax
	popq %rdx
	cmpq %rdx,%rax
	jng lessThanFalse5
	movq $1,%rax
	jmp end12
lessThanFalse5:
	movq $0,%rax
end12:
	cmpq $1,%rax
	jne end13
	movq $2,%rax
	pushq %rax
	movq -8(%rbp),%rax
	popq %rdx
	imulq %rdx,%rax
	movq %rax,-24(%rbp)
	movq $3,%rax
	pushq %rax
	movq -16(%rbp),%rax
	popq %rdx
	subq %rdx,%rax
	movq %rax,-32(%rbp)
	movq -8(%rbp),%rax
	pushq %rax
	movq -24(%rbp),%rax
	pushq %rax
	movq -32(%rbp),%rax
	popq %rdx
	addq %rdx,%rax
	popq %rdx
	movq 8(%rdi),%rcx
	cmpq %rdx,0(%rcx)
	jng indexOutOfBoundsLabel1
	cmpq $0,%rdx
	jl indexOutOfBoundsLabel1
	movq %rax,8(%rcx,%rdx,8)
	jmp end14
indexOutOfBoundsLabel1:
	call mjerror
end14:
	movq -8(%rbp),%rax
	pushq %rax
	movq $1,%rax
	popq %rdx
	addq %rdx,%rax
	movq %rax,-8(%rbp)
	movq $1,%rax
	pushq %rax
	movq -16(%rbp),%rax
	popq %rdx
	subq %rdx,%rax
	movq %rax,-16(%rbp)
	jmp while3
end13:
	movq $0,%rax
	movq %rbp,%rsp
	popq %rbp
	ret 
	.data
LinearSearch$$: .quad 0
LS$$: .quad 0
		.quad LS$Start
		.quad LS$Print
		.quad LS$Search
		.quad LS$Init
