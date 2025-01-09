
; To use:
;
;    yasm -felf64 -gdwarf2 basicprint.asm        (if not already done)
;
;    yasm -felf64 -gdwarf2 3N+1.asm 
;    ld -g -o 3N+1 3N+1.o basicprint.o
;    ./3N+1

extern printlong, printstring   ; functions defined in basicprint.asm


section .rodata

   N: dq 27  ; The starting point for the sequence

   ct_label:  db "The number of terms in the sequence was ", 0  

   eoln: db 10, 0 
      
section .text

global _start
_start:

   mov r12, [N]       ; Get the start value for N
   mov r13, 1         ; Inititialize count to 1, to accounts for the starting value

   mov rdi, r12
   call printlong     ; Print the starting value
   mov rdi, eoln
   call printstring

.while:
   cmp r12, 1
   je .donewhile
   inc r13            ; increment count
   mov r14, r12       ; temp = N
   and r14, 1
   cmp r14, 1         ; test if N is odd
   jne .else          ; if N is not odd, go to else part
   imul r12, 3
   add r12, 1         ; N = 3N+1
   jmp .doneif
.else:
   shr r12, 1         ; N = N/2
.doneif:
   mov rdi, r12
   call printlong
   mov rdi, eoln
   call printstring
   jmp .while
   
.donewhile
   mov rdi, eoln
   call printstring
   mov rdi, ct_label
   call printstring
   mov rdi, r13
   call printlong
   mov rdi, eoln
   call printstring
   
; end the program

   mov rax, 60       ; code for "exit" syscall
   mov rdi, 0        ; argument to the syscall, indicating successful execution
   syscall

