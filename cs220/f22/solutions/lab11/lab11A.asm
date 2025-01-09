; The functions defined and used in this program follow the C calling convention.

global sum8, searchArray, fibonacci

section .text

;------------------------------------
; Define function sum8(a,b,c,d,e,f,g,h).  Arguments and return value are of type long

sum8:
   mov rax, rdi
   add rax, rsi
   add rax, rdx
   add rax, rcx
   add rax, r8
   add rax, r9
   add rax, [rsp+8]
   add rax, [rsp+16]
   ret

;-------------------------------------
; Define function  searchArray(A,len,x)  that searches an array for a number.
; Arguments: A is the address of the array, len is the number of items in the
; array, x is the number to be searched for.  The return value is the array
; index where x is found, or is -1 if x is not found.

searchArray:
    ; A in rdi, len in rsi, x in rdx; return value in rax
    
    mov r12, 0    ; index
.loop:
    cmp r12, rsi  ; if index equals array length, return 1
    jne .test
    mov rax, -1
    ret
    
.test:
    cmp rdx, [rdi + r12*8]  ; if A[r12] == rdx, return r12
    jne .next
    mov rax, r12
    ret
    
.next:
    inc r12
    jmp .loop  
    
    
;---------------------------------
; Define  function fibonacci(N) that computes the Nth Fibonacci number in
; a horribly inefficient way.  The argument and return value are unsigned long.

fibonacci:
   ; N is in rdi
   
   cmp rdi, 2
   jge .recur
   mov rax, 1
   ret
   
.recur:
   push rdi  ; save N on the stack
   dec rdi
   call fibonacci
   mov rdi, [rsp]
   mov [rsp], rax
   sub rdi, 2
   call fibonacci
   pop rdi
   add rax, rdi
   ret

