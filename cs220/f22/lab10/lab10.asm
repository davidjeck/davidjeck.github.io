
; Lab 10

extern printstring, printlong, printulong

section .rodata

; define some strings
    eoln:     db  10, 0     ; end-of-line
    space:    db  32, 0     ; a single space character
    advice:   db  "Every Exercise is Important for Learning!", 0
    
    
section .data

; define a list of numbers

    LIST:      dq  211,718,62,229,445,137,73,215,790,355,801,375,100,299,\
                   574,476,94,753,695,830,412,77,108,82,534,630,34,21,636,\
                   394,377,356,535,620,182,655,238
    LISTlen:   dq  37


section .text

global  _start
_start:
    

    
Exercise1:       ; Use a loop to output  1 2 3 4 5 6 7 8 9 10




Exercise2:       ; Count the e's
        



Exercise3:       ; Fizz-Buzz

         


Exercise4:       ; Maximum of an array
        



Exercise5:       ; Selection Sort
        




; EXIT -- end of program    

        mov rax, 60  ; code for exit syscall
        mov rdi, 0   ; exit success
        syscall
    

