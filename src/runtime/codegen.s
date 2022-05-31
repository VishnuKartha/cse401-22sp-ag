        .text
        .globl asm_main
asm_main:
        pushq %rbp
        movq %rsp,%rbp
        pushq %rdi
        movq %rdi,%rbx
        pushq %rdi
        movq $8,%rdi
        call mjcalloc
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
        call put
        popq %rdx
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
        call mjcalloc
        popq %rdx
        popq %rdx
        popq %rdi
        movq %rdx,0(%rax)
        movq %rax,-8(%rbp)
        movq $5,%rax
        pushq %rax
        movq $3,%rax
        popq %rdx
        movq -8(%rbp),%rcx
        cmpq %rdx,0(%rcx)
        jng indexOutOfBoundsLabel1
        cmpq $0,%rdx
        jl indexOutOfBoundsLabel1
        movq %rax,8(%rcx,%rdx,8)
        jmp end1
indexOutOfBoundsLabel1:
        pushq %rax
        call mjerror
        popq %rdx
end1:
        movq $3,%rax
        movq %rbp,%rsp
        popq %rbp
        ret
        .data
Factorial$$: .quad 0
One$$: .quad 0
                .quad One$test
