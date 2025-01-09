
; Prints a string of 60 random dots and dashes, in two ways.
; Sample output:
;    -.---....-.---..-.--..--.-..--....---.---..-.----.--.--...-.
;    -.--.-.--..---.-.--..-...------.-..-------.--..-......-.-.--
;
; This program can be assembled and linked using my build script:
;
;    /classes/cs220/build.sh -c dots-n-dashes.asm
;
; The "-c" option is required since the program uses functions from
; the C standard library.  To assemble and link it by hand, use
; 
;    yasm -felf64 -gdwarf2 dots-n-dashes.asm
;    ld -g -o dots-n-dashes dots-n-dashes.o -lc --dynamic-linker /lib/x86_64-linux-gnu/ld-linux-x86-64.so.2

global _start

extern time, printf, rand, srand, malloc, free

section .data
    
    chfmt db "%c", 0      ; format string for input/output of a single character
    eoln db 10, 0         ; a string containing just an end-of-line character
    strfmt db "%s", 10, 0 ; format string for outputting a string followed by an end-of-line
    

section .text

_start:

; Initialize the random number generator, using the current time.
; If this isn't done, every run of the program will use the same pseudorandom numbers.

   mov rdi,0   ; (a NULL pointer, the argument to the time() function)
   call time
   mov rdi,rax
   call srand
   
;------------------------------------------------------------------------------------   
; print 60 random dots and dashes, one character at a time, followed by an end-of-line
   
   mov r12, 0    ; for counting the characters that have been output
.onech:
   call rand     ; get a random number (in eax) and test bit number 4 in the number
   and eax, 0x10 ; answer is 0 or 0x10
   
   cmp eax, 0    ; if the bit is 0, load a dot into rsi; otherwise load a dash
   je .dot
   mov rsi, '-'  ; "dash"
   jmp .output
.dot:
   mov rsi, '.'  ; "dot"

.output:
   mov rdi, chfmt  ; output the char, already loaded into rsi, using format string chfmt
   call printf
   
   inc r12         ; increment the counter
   cmp r12, 60
   jl .onech       ; if fewer than 60 chars have been output, jump back to strt of loop
   
   mov rdi, eoln   ; output an end-of-line after the line of dots and dashes
   call printf
   
;------------------------------------------------------------------------------------   
; create a string of 60 random dots and dashes in newly allocated memory in the heap

Version2:

   mov rdi, 61  ; allocate space for 60 dots and dashes plus a zero at the end
   call malloc
   mov r12, rax ; save the pointer to the newly allocated memory in r12
   
   mov r13, 0   ; for counting the characters
   
.onech:
   call rand     ; get a random number (in eax) and test bit number 4 in the number
   and eax, 0x10 ; answer is 0 or 0x10
   
   cmp eax, 0    ; if the bit is 0, store a dot in memory; otherwise store a dash
   je .dot
   mov byte [r12 + r13], '-'  ; This is essentially str[r13] = '-'
   jmp .nextch
.dot:
   mov byte [r12 + r13], '.' ; This is essentially str[r13] = '.'
   
.nextch
   inc r13      ; if fewer than 60 chars have been output, jump back to strt of loop
   cmp r13, 60
   jl .onech
   
   mov byte [r12 + r13], 0  ; r13 is 60; add a zero at the end of the string
   
   mov rdi, strfmt ; output the string of characters that have been generated
   mov rsi, r12
   call printf
   
   mov rdi, r12    ; free the memory that was allocated 
   call free       ;     (not really necessary at the end of the program)


;------------------------------------------------------------------------------------   

   mov rax, 60     ; EXIT 
   mov rdi, 0
   syscall
   
