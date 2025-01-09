
; To use:
;
;    yasm -felf64 -gdwarf2 basicprint.asm        (if not already done)
;
;    yasm -felf64 -gdwarf2 buggy.asm 
;    ld -g -o buggy buggy.o basicprint.o
;    ./buggy
;
; To debug:
;    ddd buggy

extern printlong, printstring   ; functions defined in basicprint.asm


section .data

   sum_label:  db "The sum is: ", 0   ; zero-terminated string!

   eoln: db 10, 0  ; A string containing just the linefeed character, 10
   
   a:    dq   101
   b:    dq   201
   c:    dq   301

   array:     dq 1,3,5,7
   array_size:  dq  4 
   
section .text

global _start
_start:

; Compute and print the sum of the values in the variables a, b, and c

   mov rdi, sum_label
   call printstring

   mov rax, 0
   add rax, a
   add rax, b
   add rax, c
   
   mov rdi, rax
   call printlong
   mov rdi, eoln
   call printstring
   
; try again to print the sum

   mov rax, 0
   add rax, [a]
   add rax, [b]
   add rax, [c]
   
   mov rdi, sum_label
   call printstring
   mov rdi, rax
   call printlong
   mov rdi, eoln
   call printstring
   
; now try using a loop to find the sum of an array

   mov rdi, sum_label
   call printstring

   mov rax, 0              ; the sum
   mov rbx, [array_size]   ; number of items in array, for counting down
   mov rcx, array          ; memory address
   
add_one:
   add rax, [rcx]
   add rcx, 8              ; increment address to point to next item in array
   dec rbx                 ; subtract one from counter
   cmp rbx, 0              ; compare counter to 0
   jge add_one             ; jump back if not yet done

   mov rdi, rax
   call printlong
   mov rdi, eoln
   call printstring
   
; end the program

   mov rax, 60       ; code for "exit" syscall
   mov rdi, 0        ; argument to the syscall, indicating successful execution
   syscall
