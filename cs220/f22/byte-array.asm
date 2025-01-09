; This program is meant to illustrate using a 2D array of bytes.  It defines macros
; for getting an item from the array and for putting an item into the array.  The macros
; assume that the number of columns in the array is given by a constant.
; As an example, a matrix is created containing a multiplication table, and then
; the matrix is printed out.


extern printf, malloc, free
global _start


COLUMNS equ 20  ; A constant specifying the number of columns in the array.
ROWS equ 12     ; A constant specifying the number of rows in the array.



; Define a 4-argument macro  get <array_base_address> <dest> <row> <col>
; Copy one item from a 2D array of bytes into an 8-bit register.  The number
; of columns in the array must be equal to the constant COLUMNS.
; NOTE:  This macro uses the rbx register.  No other register is changed.
;   <array_base_address> could be a variable or the value from a register or memory location.
;   <dest> must be an 8-bit register
;   <row> and <col> must be in the range 0 to COLUMNS-1
; Examples:
;   get MATRIX, al, 3, 4    ; MATRIX is a variable, i.e. a label whose constant value is the address of the array
;   get [array], r15b, 0, 0 ; array is the address of a memory location that holds the address of the array
;   get [rax], cl, 11, 5    ; register rax hold the address of the array
%macro get 4        ; arguments are: array base address, byte register, row, column
   mov rbx, COLUMNS  ; This is the number of items in each row of the array
   imul rbx, %3  ; rbx now holds the offset of the start of row %3 from the start of the array
   add rbx, %4   ; rbx now holds the offset of row %3, column %4 from the start of the array
                 ; (Note: If this weren't an array of bytes, we would have to multiply here by the item size.)
   add rbx, %1   ; add on the starting address of the array
   mov %2, byte [rbx]
%endmacro


; Define a 4-argument macro  put <array_base_address> <value> <row> <col>
; Copy one 8-bit value into a 2D array of bytes.  The number
; of columns in the array must be equal to the constant COLUMNS.
; NOTE:  This macro uses the rbx register.  No other register is changed.
;   <array_base_address> could be a variable or the value from a register or memory location.
;   <value> can be an 8-bit immediate or an 8-bit register
;   <row> and <col> must be in the range 0 to COLUMNS-1
; Examples:
;   put MATRIX, 12, 3, 4
;   put [array], al, 0, 0 
;   put [rax], r15b, 11, 5
%macro put 4        ; arguments are: array base address, byte register or immediate, row, column
   mov rbx, COLUMNS
   imul rbx, %3
   add rbx, %4
   add rbx, %1
   mov byte [rbx], %2
%endmacro



section .data
   output_fmt db "%4d",0  ; For printing values from the array with printf.
   eoln db 10, 0          ; A string holding just an end-of-line.
   MATRIX dq 0            ; [MATRIX] will be the address of the array, which is allocated using malloc.



section .text

_start:

; Allocate memory for a 2D array of bytes with ROWS rows and COLUMNS columns

   mov rdi, ROWS*COLUMNS
   call malloc
   mov [MATRIX], rax
   
   ; NOTE:  As an alternative to using malloc, the array address could be defined as a label in a .bss section:
   ;    section .bss
   ;       MATRIX resb ROWS*COLUMNS
   ; In that case the get operation would be   get MATRIX, r15b, r12, r13
   ; and the put operation would be   put MATRIX, r14b, r12, r13
   ; (instead of get [MATRIX]... and put [MATRIX]...)


; Create a multiplication table in MATRIX.

    mov r12, 0  ; row number
makerow:
    mov r13, 0  ; column number
    makecol:
        mov r14, r12
        inc r14
        mov r15, r13
        inc r15
        imul r14, r15                 ; r14 is (r12+1)*(r13+1)
        put [MATRIX], r14b, r12, r13  ; MATRIX[r12][r13] = r14
        inc r13
        cmp r13, COLUMNS
        jl makecol 
     inc r12
     cmp r12, ROWS
     jl makerow
     
     
; Output MATRIX in neat rows and columns.

    mov r15, 0  ; ensure all bits in r15 are zero before loading values into r15b (so that full r15 register can be ued for printing)
     
    mov r12, 0  ; row number
putrow:
    mov r13, 0  ; column number
    putcol:
        mov rdi, output_fmt
        get [MATRIX], r15b, r12, r13  ; r15b = MATRIX[r12][r13]
        mov rsi, r15  ; (we just changed low-order 8 bits, but higher-order bits are still zero)
        call printf
        inc r13
        cmp r13, COLUMNS
        jl putcol 
     mov rdi, eoln
     call printf
     inc r12
     cmp r12, ROWS
     jl putrow
     
     
; Free the memory used by MATRIX (not really necessary at this point, since program is ending).

   mov rdi, [MATRIX]
   call free
   
   mov rax, 60   ; EXIT
   mov rdi, 0
   syscall

