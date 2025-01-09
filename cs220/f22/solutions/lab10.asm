; Lab 10, submitted by  <put your name here!>

; To use:
;
;    yasm -felf64 -gdwarf2 basicprint.asm        (if not already done)
;
;    yasm -felf64 -gdwarf2 lab10.asm 
;    ld -g -o lab10 lab10.o basicprint.o
;    ./lab10

extern printstring, printlong, printulong

section .rodata

; define some strings
    eoln:     db  10, 0     ; end-of-line
    space:    db  32, 0     ; a single space character
    advice:   db  "Every Exercise is Important for Learning!", 0
    
    label2:   db  "The number of E's in the string is ", 0
    label3:   db  "The Fizz-Buzz problem:", 10, 0
    fizz:     db  "Fizz", 0
    buzz:     db  "Buzz", 0
    fizzbuzz: db  "Fizz Buzz", 0
    label4:   db  "The maximum of the array is ", 0
    label5:   db  "The array in sorted order:", 10, 0
    
section .data

; define a list of numbers

    LIST:      dq  211,718,62,229,445,137,73,215,790,355,801,375,100,299,\
                   574,476,94,753,695,830,412,77,108,82,534,630,34,21,636,\
                   394,377,356,535,620,182,655,238
    LISTlen:   dq  37


section .text

global _start
_start:
    

    
Exercise1:       ; Use a loop to output  1 2 3 4 5 6 7 8 9 10

        mov r12, 1          ; r12 = 1
    .while:                 ; while (r12 <= 10)
        cmp r12, 10
        jg .done
        mov rdi, r12        ;    output r12
        call printlong
        mov rdi, space      ;    output a space
        call printstring  
        inc r12             ;    r12++
        jmp .while          ; end while loop
    .done:
        mov rdi, eoln       ; output two end-of-line's
        call printstring
        mov rdi, eoln
        call printstring




Exercise2:       ; Count the e's

        mov r12, 0          ; r12 = 0; r12 counts the E's
        mov r13, advice     ; r13 = string address
    .while:                 ; while char at address r13 is not zero
        cmp byte [r13], 0
        je .done
        cmp byte [r13], 'e' ; if the character an e, increment counter
        jne .elseif
        inc r12
    .elseif:
        cmp byte [r13], 'E' ; else if the character is an E, increment counter
        jne .endif
        inc r12
    .endif:
        inc r13             ; increment the address in any case
        jmp .while          ; end of while loop
    .done:
        mov rdi, label2     ; output a lable for the count
        call printstring
        mov rdi, r12        ; output the count
        call printlong
        mov rdi, eoln       ; output an end-of-line
        call printstring
        



Exercise3:       ; Fizz-Buzz

        mov rdi,eoln
        call printstring
        mov rdi, label3
        call printstring
        mov r12, 1          ; r12 = 1
    .while:
        cmp r12, 100
        jg .done
        mov rdx, 0
        mov rax, r12
        mov rcx, 15
        div rcx
        cmp rdx, 0         ; test if r12 is divisible by 15
        jne .fizz
        mov rdi, fizzbuzz  ; if so, print "Fizz Buzz"
        call printstring
        jmp .endif
     .fizz:
        mov rdx, 0
        mov rax, r12
        mov rcx, 3
        div rcx
        cmp rdx, 0         ; test if r12 is divisible by 3
        jne .buzz
        mov rdi, fizz      ; if so, print "Fizz"
        call printstring
        jmp .endif
     .buzz:
        mov rdx, 0
        mov rax, r12
        mov rcx, 5
        div rcx
        cmp rdx, 0         ; test if r12 is divisible by 5
        jne .number
        mov rdi, buzz      ; if so, print "Buzz"
        call printstring
        jmp .endif
     .number:
        mov rdi, r12       ; in the default case, print the number.
        call printlong
     .endif:
        mov rdi, eoln
        call printstring
        inc r12
        jmp .while
     .done:
        mov rdi,eoln
        call printstring
         



Exercise4:       ; Maximum of an array

        mov r12, LIST       ; get address of start of list
        mov r13, [LIST]     ; max = [LIST]
        mov r14, 1          ; count = 1
    .while:
        cmp r14, [LISTlen]  ; while count <= LISTlen
        jg .done
        cmp r13, [r12]      ; compare max, [LIST]
        jge .endif
        mov r13, [r12]      ; if max is not >= [LIST], replace max with [LIST]
    .endif:
        inc r14             ; count++
        add r12, 8          ; LIST = LIST + 8 , to advance LIST to next array element
        jmp .while
    .done:
        mov rdi, label4     ; print the max, with a label and two ends-of-line
        call printstring
        mov rdi, r13
        call printlong
        mov rdi, eoln
        call printstring
        mov rdi, eoln
        call printstring
        



Exercise5:       ; Selection Sort
        
        ;  r12 is top
        ;  r13 is i
        ;  r14 is max
        ;  r15 is maxloc
        ;  rbx is temp

        mov r12, [LISTlen]         ; top = Listlength - 1
        dec r12
     .outerloop:
        cmp r12, 0                 ; while top >= 0
        jle .endouter
        mov r14, [LIST]            ;   max = LIST[0]
        mov r15, 0                 ;   maxloc = 0
        mov r13, 1                 ;   i = 1
     .innerloop:
           cmp r13, r12            ;   while i <= top
           jg .endinner
           cmp [LIST+r13*8],r14    ;      if LIST[i] > max
           jle .nochange         
           mov r14, [LIST+r13*8]   ;          max = LIST[i]
           mov r15, r13            ;          maxloc = i
        .nochange:
           inc r13                 ;      i++
           jmp .innerloop
     .endinner:
        mov rbx, [LIST+r12*8]      ;   temp = LIST[top]
        mov [LIST+r15*8], rbx      ;   LIST[i] = temp
        mov [LIST+r12*8], r14      ;   LIST[top] = max
        dec r12                    ;   top--
        jmp .outerloop
     .endouter:
     
printlist:
       mov rdi, label5          ; Print a label for the output.
       call printstring
       mov r12, 0               ; i = 0
   .while:
       cmp r12, [LISTlen]       ; while ( i < LISTlen )
       je .done
       mov rdi, [LIST+r12*8]    ; print LIST[i]
       call printlong
       mov rdi, eoln
       call printstring
       inc r12                  ; i++
       jmp .while
   .done:

; EXIT -- end of program    
    mov rax, 60  ; code for exit syscall
    mov rdi, 0   ; exit success
    syscall
    

