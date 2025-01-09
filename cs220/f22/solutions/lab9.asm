; solution for Lab 9 (the first yasm lab)

extern printlong, printulong, printhex, printstring   ; functions defined in basicprint.asm


section .data

   label1: db "The value of (y - x + 12) is: ", 0 
   label2: db "The value of ((5*x + 3) * (y + z - w)) is: ", 0 
   label3: db "The quotient z/w is ", 0
   label4: db " and the remainder is ", 0
   label5: db "The sum of the list is ", 0

   eoln db 10, 0  ; A string containing just the linefeed character, 10
   
   x:    dq   17
   y:    dq   42
   z:    dq   1398
   w:    dq   93

   list: dq 100,27,53,20
   
section .text

global _start
_start:

; print -1 as a signed decimal, unsigned decimal, and hexadecimal number

   mov rdi, -1
   call printlong
   mov rdi, eoln
   call printstring
   
   mov rdi, -1
   call printulong
   mov rdi, eoln
   call printstring
   
   mov rdi, -1
   call printhex
   mov rdi, eoln
   call printstring
   
; Compute and print the value of the expression  y - x + 12

   mov rdi, label1
   call printstring
   mov rdi, [y]
   sub rdi, [x]
   add rdi, 12
   call printlong
   mov rdi, eoln
   call printstring
   
; Compute and print the value of the expression  (5*x + 3) * (y + z - w)

   mov rdi, label2
   call printstring
   mov rax, [x]
   mov rbx, 5
   mul rbx
   add rax, 3
   mov rbx, rax
   mov rax, [y]
   add rax, [z]
   sub rax, [w]
   mul rbx
   mov rdi, rax
   call printlong
   mov rdi, eoln
   call printstring
   
; Compute and print the quotient and remainder for z/w

   mov rax, [z]
   mov rdx, 0
   mov rbx, [w]
   div rbx
   mov r12, rax
   mov r13, rdx
   mov rdi, label3
   call printstring
   mov rdi, r12
   call printlong
   mov rdi, label4
   call printstring
   mov rdi, r13
   call printlong
   mov rdi, eoln
   call printstring
   
; Compute the sum of the list without using a loop, and print it

   mov rdi, label5
   call printstring
   mov rdi, 0
   add rdi, [list]
   add rdi, [list+8]
   add rdi, [list+16]
   add rdi, [list+24]
   call printlong
   mov rdi, eoln
   call printstring
      
   
; end the program

   mov rax, 60       ; code for "exit" syscall
   mov rdi, 0        ; argument to the syscall, indicating successful execution
   syscall
