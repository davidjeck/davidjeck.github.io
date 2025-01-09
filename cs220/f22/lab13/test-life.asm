
global _start
extern next_generation
extern printf


; Define a 4-argument macro  get <array_base_address> <dest> <row> <col>
; Copy one item from a 2D array of bytes into an 8-bit register.  The number
; of columns in the array must be equal to the constant COLUMNS.
; NOTE:  This macro uses the rbx register.  No other register is changed.
;   <array_base_address> could be a variable or the value from a register or memory location.
;   <dest> must be an 8-bit register
;   <row> and <col> must be in the range 0 to COLUMNS-1
%macro get 4 
   mov rbx, 64 
   imul rbx, %3
   add rbx, %4
   add rbx, %1
   mov %2, byte [rbx]
%endmacro


; Define a 4-argument macro  put <array_base_address> <value> <row> <col>
; Copy one 8-bit value into a 2D array of bytes.  The number
; of columns in the array must be equal to the constant COLUMNS.
; NOTE:  This macro uses the rbx register.  No other register is changed.
;   <array_base_address> could be a variable or the value from a register or memory location.
;   <value> can be an 8-bit immediate or an 8-bit register
;   <row> and <col> must be in the range 0 to COLUMNS-1
%macro put 4  
   mov rbx, 64
   imul rbx, %3
   add rbx, %4
   add rbx, %1
   mov byte [rbx], %2
%endmacro


section .rodata

   fmt_ch db "%c", 0   ; for I/O of a single character
   eoln db 10, 0       ; end-of-line

section .bss

   board resb 64*64
   
section .text

_start:

; Fill the board with '.', 8 bytes at a time

   mov r12, 0          ; counter
   mov r13, board      ; pointer into the array
   mov r14, "........" ; eight bytes, each containing a "."
fill:
   cmp r12, 64*64/8
   jge donefill
   mov qword [r13], r14
   add r13, 8
   inc r12
   jmp fill
donefill:

; Put an R-pentomino-shaped groupd of '*' in the center of the board
   put board, '*', 31, 31    ; board[31][31] = '*'
   put board, '*', 31, 32
   put board, '*', 32, 32
   put board, '*', 32, 33
   put board, '*', 33, 32
   
   
; Run 100 generations of the game of life!

runlife:

   mov r12, 0  ; count the generations.
.gen:
   mov rdi, board
   mov rsi, 64     ; number of rows (to be ignored except for extra credit part)
   mov rdx, 64     ; number of columns (to be ignored except for extra credit part)
   call next_generation
   inc r12
   cmp r12, 100
   jl .gen
   
   
; Print the resulting board

printboard:

   mov r12, 0       ; row number
.row:
   cmp r12, 64
   jge .doneprint
   mov r13, 0       ; column number
   .col:
      cmp r13, 64
      jge .nextrow
      mov rdi, fmt_ch
      get board, sil, r12, r13   ; sil = board[r12][r13]  (sil is lower 8 bits of rsi)
      call printf
      inc r13
      jmp .col
.nextrow:
   mov rdi, eoln
   call printf
   inc r12
   jmp .row
.doneprint:

   mov rax, 60   ; EXIT
   mov rdi, 0
   syscall
   
