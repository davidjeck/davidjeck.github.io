
; This program computes and prints the factorial of n for n from 0 to 20.
; Note that larger values of n can't be used because for n bigger than 20,
; the factorial of n requires more than 64 bits.

; This program must be linked with basicprint.asm.

; This program uses a non-standard calling convention:
;        -- the caller pushes the argument onto the stack before calling the function
;        -- the function places the return value into rax
;        -- the caller is responsible for removing the argument from the stack


global _start
extern printulong, printstring

section .data

   label: db " factoial is ", 0
   eoln:  db 10, 0

section .text

_start:

   mov rbx, 0         ; rbx holds N, and is incremented from 0 to 20
   
.loop:
   
   push rbx           ; copy N from rbx onto the stack, as the argument for the function
   call factorial     ; call the factorial function
   add rsp, 8         ; remove N from the stack
   
   ; The value of factorial(N) is in rax

   push rax           ; save rax while printing n
   mov rdi, rbx       ; print N
   call printulong
   mov rdi, label     ; print a lable for the output
   call printstring
   pop rdi            ; retrieve factorial(N) from the statck into rdi, and print it
   call printulong
   mov rdi, eoln
   call printstring
   
   inc rbx            ; increment N and repeat the loop if it is less than or equal to 20
   cmp rbx, 20
   jle .loop
   
   mov rax, 60        ; EXIT
   mov rdi, 1
   syscall


;--------------------------
; Define the factorial function.  N is on the stack, under the return address.
; compute factorial(N), and leave it in register rax.

factorial:
   cmp qword [rsp+8],0    ; if N is not 0, jump to .recur
   jne .recur
   mov rax, 1             ;    in the case N = 0, just return 1 in rax
   ret
.recur:                   ; else case: return N*factorial(N-1)
   mov rax, [rsp+8]       ;    copy N from the stack
   dec rax                ;    subtract 1
   
   push rax               ;    compute factorial(N-1)
   call factorial
   add rsp, 8

   imul rax, [rsp+8]      ;    rax contains factorial(N-1); mutiply it by N
   ret

