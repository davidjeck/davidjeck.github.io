; This program will read simple expressions such as 2+2 and 35 - 187 from the user
; and print their value.  The expression must consist of two integers with an
; operator between them.  The operator can be +, -, *, /, or %.  Whitespace is
; ignored, and extra input on a line is ignored.  The program can be ended by
; typing Control-C (or by dividing by zero, which will crash the program).
; Note that this program must be linked with the standard Linux C library.


extern printf, scanf, getchar

section .data
  
   prompt: db ">> ", 0
   expfmt: db "%ld %c %ld", 0
   outfmt: db "Result is:  %ld %c %ld  =  %ld", 10, 0
   error: db "Input contains an error.  Try again.", 10, 0
   badop: db "Operator not recognized.  Try again with + or *", 10, 0
   A dq 0
   B dq 0
   op db 0
  
section .text

global _start
_start:

   mov rdi, prompt       ; output ">>" as a prompt for user input
   call printf
   
   mov rdi, expfmt       ; read the numbers into A and B and the operator into op
   mov rsi, A
   mov rdx, op
   mov rcx, B
   call scanf
   
   cmp eax, 3            ; if the return value from scanf is not 3, there was an error input
   je ans
   mov rdi, error        ; Report that there was an error, and jump to clearline to discard input
   call printf
   jmp clearline
   
ans:   ; compute the answer
   mov rax, [A]          ; Copy first number to rax, then apply op to rax and the second number
   cmp byte[op], '+'
   je addthem
   cmp byte[op], '-'
   je subthem
   cmp byte[op], '*'
   je multhem
   cmp byte[op], '/'
   je divthem
   cmp byte[op], '%'
   je remthem
   mov rdi, badop        ; The op is not one of the 5 legal values, report an error and discard input
   call printf
   jmp clearline
addthem:
   add rax, [B]
   jmp output
subthem:
   sub rax, [B]
   jmp output
multhem:
   imul rax, [B]
   jmp output
divthem:
   cqo
   idiv qword [B]
   jmp output
remthem:
   cqo
   idiv qword [B]
   mov rax, rdx

output:   ; output the result, including both the expression and the answer
   mov rdi, outfmt
   mov rsi, [A]
   mov rdx, [op]
   mov rcx, [B]
   mov r8, rax
   call printf
   ; Note that the rest of the input line is read, to discard any extra chars on the input line
   
clearline:  ; read and discard the rest of the input line, then go back to do the next input
   call getchar
   cmp eax, 10
   jne clearline
   jmp _start
   
