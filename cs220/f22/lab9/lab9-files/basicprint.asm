
; Define the functions   void printlong(long n)
;                        void printulong(unsigned long n)
;                        void printhex(unsigned long n)
;                        void printstring(char* str)

; to be assembled with:  yasm -felf64 -gdwarf2 basicprint.asm


section .text

;---------------------------------------------------------------------------------------
; Define void printlong(long n) for printing signed integers.
; Note:  n is passed in register rdi, according to C calling convention.
; n is converted to its base-10 string representation, stored temporarily on the stack.

global printlong
printlong:
   mov rcx, rsp        ; save the stack pointer in rcx
   dec rsp             ; make space on stack for boolean value, to check if n is negative
   mov byte [rsp], 0   ; store 0 (false) in space just allocated on stack
   mov rax, rdi        ; copy n from rdi to rax
   cmp rdi, 0          ; compare n to 0
   jge print           ; if n > 0, jump to print (skipping the next two instructions)
   mov byte [rsp], 1   ; store 1 (true) on stack, replacing the 0
   neg rax             ; n = -n (negate rax, so it contains the absolute value of original n)
   
; Now, convert rax, which is abs(n), to its string representation, stored on the stack
  
print:
   mov r8, 0           ; r8 will be the number of chars in the output string
   mov r9, 10          ; for dividing by 10
.next_digit:
   mov rdx, 0          ; rdx must be 0 before using the div instruction
   div r9              ; divide n by 10 (rax = rax / r9, with remainder in rdx)
   inc r8              ; add 1 to r8 to account for this digit in the output
   dec rsp             ; make space on the stack for this digit
   mov byte [rsp], dl  ; copy low-order byte from rdx to stack
   add byte [rsp], 48  ; add 48 to convert from digit to corresponding char (eg:  7 -> '7')
   cmp rax, 0          ; compare n to 0
   ja .next_digit       ; if n > 0, jump to next_digit (we are done when n reaches 0)
   
; If the original n was negative, add a "-" to the string
   
   cmp byte [rcx-1], 0 ; test if original n was negative (rcx-1 is the location of the sign bit on the stack)
   je .done             ; if sign bit is 0, n is positive, jump to done
   inc r8              ; if number was negative, push the character '-' onto the stack
   dec rsp             ; make space for the '-' on the tack 
   mov byte [rsp], 45  ; 45 is the character '-'
   
; print the string, which is on the stack
   
.done:
   mov rax, 1          ; syscall code for write string
   mov rdi, 1          ; STDOUT, destination for output
   mov rsi, rsp        ; location of string is given by rsp
   mov rdx, r8         ; length of string is given by r8
   push rcx            ; save rcx on the stack, since the sycall will change its value
   syscall             ; print the string, which represents the original n
   pop rcx             ; restore value of rcx from the stack
   
   mov rsp, rcx        ; restore the stack pointer
   ret                 ; return from function
   
;-----------------------------------------------------------------------------------
; Define void printulong(unsigned long n) for printing unsigned long integers.
; Note:  n is passed in register rdi, according to C calling convention.
; n is converted to its base-10 string representation, stored temporarily on the stack.
   
global printulong
printulong:
   mov rcx, rsp        ; save the stack pointer in rcx
   mov rax, rdi        ; copy n from rdi to rax
   
; Now, convert rax, which is n, to its string representation, stored on the stack
  
   mov r8, 0           ; r8 will be the number of chars in the output string
   mov r9, 10          ; for dividing by 10
.next_digit:
   mov rdx, 0          ; rdx must be 0 before using the div instruction
   div r9              ; divide n by 10 (rax = rax / r9, with remainder in rdx)
   inc r8              ; add 1 to r8 to account for this digit in the output
   dec rsp             ; make space on the stack for this digit
   mov byte [rsp], dl  ; copy low-order byte from rdx to stack
   add byte [rsp], 48  ; add 48 to convert from digit to corresponding char (eg:  7 -> '7')
   cmp rax, 0          ; compare n to 0
   ja .next_digit     ; if n > 0, jump to next_digit (we are done when n reaches 0)
      
; print the string, which is on the stack

   mov rax, 1          ; syscall code for write string
   mov rdi, 1          ; STDOUT, destination for output
   mov rsi, rsp        ; location of string is given by rsp
   mov rdx, r8         ; length of string is given by r8
   push rcx            ; save rcx on the stack, since the sycall will change its value
   syscall             ; print the string, which represents the original n
   pop rcx             ; restore value of rcx from the stack
   
   mov rsp, rcx        ; restore the stack pointer
   ret                 ; return from function
   
;-----------------------------------------------------------------------------------
; Define void printhex(unsigned long n) for printing unsigned long integers in hexadecimal.
; Note:  n is passed in register rdi, according to C calling convention.
; n is converted to its base-16 string representation, stored temporarily on the stack.
   
global printhex
printhex:
   mov rcx, rsp        ; save the stack pointer in rcx
   mov rax, rdi        ; copy n from rdi to rax
   
; Now, convert rax, which is n, to its hex string representation, stored on the stack
  
   mov r8, 0           ; r8 will be the number of chars in the output string
   mov r9, 16          ; for dividing by 16
.next_digit:
   mov rdx, 0          ; rdx must be 0 before using the div instruction
   div r9              ; divide n by 10 (rax = rax / r9, with remainder in rdx)
   inc r8              ; add 1 to r8 to account for this digit in the output
   dec rsp             ; make space on the stack for this digit
   mov byte [rsp], dl  ; copy low-order byte from rdx to stack
   cmp dl,10           ; if less than 10, convert to digit 0-9; if greater than or equal, convert to letter A-F
   jb .less_than_10
   add byte [rsp], 55  ; add 55 to convert from digit to corresponding hex char in range 'A' to 'F' (eg:  12 -> 'C')
   jmp .cont
.less_than_10;
   add byte [rsp], 48  ; add 48 to convert from digit to corresponding char in range '0' to '9' (eg:  7 -> '7')
.cont:
   cmp rax, 0          ; compare n to 0
   ja .next_digit     ; if n > 0, jump to next_digit (we are done when n reaches 0)
      
; print the string, which is on the stack

   mov rax, 1          ; syscall code for write string
   mov rdi, 1          ; STDOUT, destination for output
   mov rsi, rsp        ; location of string is given by rsp
   mov rdx, r8         ; length of string is given by r8
   push rcx            ; save rcx on the stack, since the sycall will change its value
   syscall             ; print the string, which represents the original n
   pop rcx             ; restore value of rcx from the stack
   
   mov rsp, rcx        ; restore the stack pointer
   ret                 ; return from function
   
;------------------------------------------------------------------------------
; Define void printstring(char* str) for printing zero-terminated strings.
; Note:  The address, str, is passed in register rdi, according to C calling convention.

global printstring
printstring:
   mov rsi, rdi        ; rsi is the address of the string
   mov rdx, 0          ; rdx will be the length of the string

; find the length of the string by incrementing rdi and rdx until a zero is found in memory

test_byte:
   cmp byte [rdi], 0
   je .done
   inc rdi
   inc rdx
   jmp test_byte

; print the string (string length and location are already in rsi and rdx)
   
.done:
   mov rax, 1          ; syscall code for write string
   mov rdi, 1          ; STDOUT, destination for output
   syscall
   
   ret


