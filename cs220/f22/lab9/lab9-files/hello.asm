; This is a "main program" that prints the message "Hello World!".
; It uses the function printstring from basicprint.asm.


; To use:
;
;    yasm -felf64 -gdwarf2 basicprint.asm        (if not already done)
;
;    yasm -felf64 -gdwarf2 hello.asm 
;    ld -g -o hello hello.o basicprint.o
;    ./hello

extern printstring   ; printstring function is defined in basicprint.asm

section .data

   greeting: db "Hello World!", 0  ; A string with the required zero byte at the end
   
   eoln: db 10, 0  ; A string containing just the linefeed character, 10

; Main program
section .text

global _start
_start:

   mov rdi, greeting ; store the address of the string "Hello World!" in register rdi
   call printstring  ; call the printstring function to print the string

   mov rdi, eoln     ; store the address of the end-of-line string in register rdi
   call printstring  ; call the printstring function to print the string

   mov rax, 60       ; code for "exit" syscall
   mov rdi, 0        ; argument to the syscall, indicating successful execution
   syscall
