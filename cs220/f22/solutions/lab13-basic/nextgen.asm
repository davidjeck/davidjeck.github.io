
; This file defines the function   void next_generation( char[][64] board ).
; The parameter must be a 64-by-64 array of chars representing a board in
; Conway's Game of Life, with a '.' representing a dead cell and a '*'
; representing an alive cell.  The next generation of the board is computed,
; and it replaces the current generation in the board array.  

global next_generation

; Define a 4-argument macro  get <array_base_address> <dest> <row> <col>
; Copy one item from a 2D array of bytes into an 8-bit register.  The number
; of columns in the array must be equal to 64.
; NOTE:  This macro uses the rbx register.  No other register is changed.
;   <array_base_address> could be a variable or the value from a register or memory location.
;   <dest> must be an 8-bit register
;   <row> and <col> must be in the range 0 to 63
%macro get 4 
   mov rbx, 64 
   imul rbx, %3
   add rbx, %4
   add rbx, %1
   mov %2, byte [rbx]
%endmacro


; Define a 4-argument macro  put <array_base_address> <value> <row> <col>
; Copy one 8-bit value into a 2D array of bytes.  The number
; of columns in the array must be equal to 64.
; NOTE:  This macro uses the rbx register.  No other register is changed.
;   <array_base_address> could be a variable or the value from a register or memory location.
;   <value> can be an 8-bit immediate or an 8-bit register
;   <row> and <col> must be in the range 0 to 63
%macro put 4  
   mov rbx, 64
   imul rbx, %3
   add rbx, %4
   add rbx, %1
   mov byte [rbx], %2
%endmacro


section .bss

   newboard resq 64*64 ; temporarily holds the next generation as it being computed


section .text

; Computes the next generation in the game of life, based on the current generation.
; The only parameter is the address of the board, containing the current generation.
; The contents of the board are replaced with the next genertion.
next_generation:

   push r12   ; save values from callee-saved registers that will be used by next_generation
   push r13
   push r14
   push r15
   push rbx
   mov r8, 0       ; r8 will always be row number
.row:
   cmp r8, 64
   jge .copyboard
   mov r9, 0       ; r9 will always be column number
   .col:
      cmp r9, 64
      jge .nextrow
      call next_state  ; computes next state for this cell, and stores it in newboard
      inc r9
      jmp .col
.nextrow:
   inc r8
   jmp .row
   
.copyboard:   ; copy the newly computed generatin from newboard back to the actual board array
   mov r8, 0
.nextquad:    ; copy 8 bytes at a time
   cmp r8, 64*64  
   jge .done
   mov r9, [newboard+r8]  ; get next 8 bytes from newboard
   mov [rdi+r8], r9       ; copy those bytes into the board
   add r8, 8
   jmp .nextquad

.done:
   pop rbx   ; restore the saved register values.
   pop r15
   pop r14
   pop r13
   pop r12
   ret


; Compute the next state of one cell, and store it in newboard.
; This function does not use arguements or return a value.
; rdi is address of board; r8 is row r9 is column of the cell.
next_state:
   call count_neighbors  ; returns number of neighbors in al
   get rdi, cl, r8, r9
   put newboard, cl, r8, r9  ; copy old state
   cmp cl, '*'
   je .nowalive
.nowdead:      ; if old state was dead and neighbor count is 3, cell comes alive
   cmp al, 3
   jne .done
   put newboard, '*', r8, r9
   jmp .done
.nowalive:   ; if old date was alive and neighbor count is not 2 or 3, cell dies
   cmp al, 2
   je .done
   cmp al, 3
   je .done
   put newboard, '.', r8, r9
.done:
   ret


; Count the number of neigbors of a cell, wrapping around to the
; opposite edge for cells on an edge of a board.  Return the
; count in 8-bit register register al.
; rdi is address of board; r8 is row; r9 is column.
count_neighbors:
    mov al, 0     ; for counting living neighbors
    
    ; Get row and column numbers for neighboring cells.
   
    mov r12, r8   ; r12 (up) = row - 1 
    dec r12
    mov r13, r8   ; r13 (down) = row + 1
    inc r13
    mov r14, r9   ; r14 (left) = column - 1
    dec r14
    mov r15, r9   ; r15 (right) = column + 1
    inc r15
   
    ; If neighboring row/column number is off the board, change it to the
    ; opposite edge of the board.
   
    cmp r12, -1   ; if up == -1, up = 63
    jne .a
    mov r12, 63
.a: cmp r13, 64   ; if down == 64, down = 0
    jne .b
    mov r13, 0
.b: cmp r14, -1   ; if left == -1, left = 63
    jne .c
    mov r14, 63
.c: cmp r15, 64   ; if right == 64, right = 0
    jne .d
    mov r15, 0
    
    ; Check each neighboring cell state, and if it is alive, add 1 to al
    
.d: 
    get rdi, cl, r12, r14
    cmp cl, '*'
    jne .e
    inc al
.e: get rdi, cl, r12, r9
    cmp cl, '*'
    jne .f
    inc al
.f: get rdi, cl, r12, r15
    cmp cl, '*'
    jne .g
    inc al
.g: get rdi, cl, r8, r14
    cmp cl, '*'
    jne .h
    inc al
.h: get rdi, cl, r8, r15
    cmp cl, '*'
    jne .i
    inc al
.i: get rdi, cl, r13, r14
    cmp cl, '*'
    jne .j
    inc al
.j: get rdi, cl, r13, r9
    cmp cl, '*'
    jne .k
    inc al
.k: get rdi, cl, r13, r15
    cmp cl, '*'
    jne .done
    inc al
    
    ; return, with the neighbor count in al
    
.done:
   ret

