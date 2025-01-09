; This file defines a program that lets the user run Conway's Game of Life.  The program presents
; the user with a menu of actions, including making a random board, reading a board from a file,
; writing the board to a file, and computing new generations of the board.
;
; This file depends on nextgen.asm to define the next_generation function, and it must be linked
; to the C standard library.  The program can be assembled and linked using the build script:
;
;       ./build.sh -c life_main.asm next_generation.asm
;
; or directly:
;
;       yasm -felf64 -gdwarf2 life_main.asm
;       yasm -felf64 -gdwarf2 next_generation.asm
;       ld -g -o life_main life_main.o next_generation.o -l c --dynamic-linker /lib/x86_64-linux-gnu/ld-linux-x86-64.so.2
;
; The file format is as follows:  two integers giving the number of rows and the number of columns
; in the board, separated by whitespace; then one '.' or '*' for each cell in the board.  Whitespace
; is ignored when reading a file, but ordinarily, the first line of the file contains the row and
; column numbers and each line after that holds the content of one row of the board.  The files
; input_board.txt and input_board_2.txt contain sample boards in this format.

global _start
extern next_generation
extern srand, time, rand, printf, scanf, getchar, malloc, free, fscanf, fprintf, fopen, fclose, access

section .rodata

   menu db 10, "Menu:", 10, 10, \
           "   1. EXIT", 10,\
           "   2. Make Random Board", 10, \
           "   3. Read Board from File", 10, \
           "   4. Make Next Generation", 10, \
           "   5. Run for Multiple Generations", 10, \
           "   6. Print the Board", 10, \
           "   7. Write the Board to a File", 10, 10, \
           "Enter the number of your choice: ", 0
           
   start_msg db "Welcome to Life!", 10, "Starting with a random 32-by32 board.", 10, 0
   prompt_file_name db "Enter file name (no spaces allowed): ", 0
   prompt_board_size db "How many rows and columns (two integers separated by a space): ", 0
   prompt_num_gens db "Enter the number of generations: ", 0
   error_bad_menu db "Please enter an integer between 1 and 7!", 10, 0
   error_bad_size db "Illegal input! Try again.", 10, 0
   error_bad_gens db "Please enter a positive integer!", 10, 0
   error_file_name db "Please enter a file name.", 10, 0
   error_cant_read db "Sorry, an error occured trying to open that file for reading.", 10, 0
   error_bad_file db "Sorry, that file does not contain a legal life board.", 10, 0
   error_exists db "Sorry, that file already exists.  Try a different output file name.", 10, 0
   error_cant_write db "Sorry, an error occured trying to open that file for writing.", 10, 0
   
   ok db "OK", 10, 0
   eoln db 10, 0
   
   fmt_str db "%s", 0
   fmt_int db "%ld", 0
   fmt_two_int db "%ld %ld", 0
   fmt_ch db " %c", 0
   fmt_ch_out db "%c", 0
   
   mode_read db "r", 0
   mode_write db "w", 0
   
section .bss

   board resq 1     ; holds the address of the board
   num resq 1       ; a number entered by the user
   rows resq 1      ; number of rows in the board
   columns resq 1   ; number of columns in the board
   cells resq 1     ; rows*columns -- number of cells in the board
   newboard resq 1  ; board being read from a file
   filename resb 100
   chr resb 1       ; one character read from file
           
section .text

_start:

   mov rdi, 0                     ; initialize the random number generator
   call time
   mov edi, eax
   call srand
   
   mov qword [rows], 32           ; create a random 32-by-32 board
   mov qword [columns], 32
   mov rdi, 32*32
   call malloc
   mov [board], rax
   call random_fill
      
   mov rdi, start_msg             ; greet the user
   call printf

do_menu:                          ; print the menu, read the user's choice, and execute it
   mov rdi, menu
   call printf
    
   mov rdi, fmt_int
   mov rsi, num
   call scanf
   mov r15, rax
   call eatline
   cmp r15, 1
   jne bad_menu
   mov r15, [num]
   
m1:                                     ; Menu choice 1:  Exit from the program
   cmp r15, 1
   jl bad_menu
   jg m2
   mov rax, 60  ; menu item 1, EXIT
   mov rdi, 0
   syscall
   
m2:                                     ; Menu choice 2:  Create a new random board
   cmp r15, 2
   jg m3
.get_size:           ; get board size from the user
   mov rdi, prompt_board_size
   call printf
   mov rdi, fmt_two_int
   mov rsi, rows
   mov rdx, columns
   call scanf
   mov r12, rax
   call eatline
   cmp r12, 2
   je .make_rand
   mov rdi, error_bad_size
   call printf
   jmp .get_size
.make_rand:
   mov rdi, [board]     ; free memory used by current board
   call free
   mov rdi, [rows]      ; allocate memory for the new board
   imul rdi, [columns]
   call malloc
   mov [board], rax
   call random_fill
   jmp do_menu
   
m3:                                     ; Menu choice 3:  Read a board from a file
   cmp r15, 3
   jg m4
.getname:     ; get the name of the input file from the user
   mov rdi, prompt_file_name
   call printf
   mov rdi, fmt_str
   mov rsi, filename
   call scanf
   mov r12, rax
   call eatline
   cmp r12, 1
   je .openfile
   mov rdi, error_file_name
   call printf
   jmp .getname
.openfile:      ; try to open the file for input
   mov rdi, filename
   mov rsi, mode_read
   call fopen
   cmp rax, 0
   jne .readfile
   mov rdi, error_cant_read
   call printf
   jmp do_menu
.readfile:      ; file has been opened, try to read board data
   mov r12, rax
   mov rdi, r12
   mov rsi, fmt_two_int
   mov rdx, rows
   mov rcx, columns
   call fscanf         ; get the number of rows and the number of columns from the file
   cmp rax, 2
   je .getcells
   jmp .badfile
.getcells:
   mov rdi, [rows]     ; get the number of cells, which is the number of bytes needed for the board
   imul rdi, [columns]
   mov [cells], rdi
   call malloc         ; allocate memory for the new board
   mov [newboard], rax
   mov r14, 0          ; counts the input characters
   mov r15, [newboard] ; address where char will be stored
.getch:
   cmp r14, [cells]    ; get next character from the file, test that it's a '.' or '*'
   jge .done
   mov rdi, r12
   mov rsi, fmt_ch
   mov rdx, chr
   call fscanf
   cmp rax, 1
   jne .badfile
   cmp byte [chr], '.'
   je .nextch
   cmp byte [chr], '*'
   jne .badfile
.nextch:
   mov bl, [chr]
   mov [r15], bl
   inc r15
   inc r14
   jmp .getch
.done:
   mov rdi, [board]  ; replace current board with board read from the file
   call free
   mov r15, [newboard]
   mov [board], r15
   mov rdi, r12      ; close the input file
   call fclose
   jmp do_menu
.badfile:  ; file has been opened, but an error occured while reading it
   mov rdi, error_cant_read
   call printf
   mov rdi, r12      ; close the input file
   call fclose
   mov rdi, [newboard]  ; discard memory used by new board
   call free
   jmp do_menu
   
m4:                                     ; Menu choice 4:  Run life for one generation
   cmp r15, 4
   jg m5
   mov rdi, [board]
   mov rsi, [rows]
   mov rdx, [columns]
   call next_generation
   mov rdi, ok
   call printf
   jmp do_menu
   
m5:                                     ; Menu choice 5:  Run life for multiple generations
   cmp r15, 5
   jg m6
.get_gens:      ; get the number of generations from the user
   mov rdi, prompt_num_gens
   call printf
   mov rdi, fmt_int
   mov rsi, num
   call scanf
   mov r12, rax
   call eatline
   cmp r12, 1
   jne .badgens
   cmp qword [num], 1
   jge .run_life
.badgens:
   mov rdi, error_bad_gens
   call printf
   jmp .get_gens
.run_life:     ; call next_generation for the specified number of times
   mov r15, 0
.nextgen:
   cmp r15, [num]
   jge .donegens
   mov rdi, [board]
   mov rsi, [rows]
   mov rdx, [columns]
   call next_generation
   inc r15
   jmp .nextgen
.donegens:
   mov rdi, ok
   call printf
   jmp do_menu
   
   
m6:                                     ; Menu choice 1:  Print the board to standard output
   cmp r15, 6
   jg m7
   mov r12, 0       ; row number
   mov r14, [board] ; address of one char
.row:
   cmp r12, [rows]
   jge .done
   mov r13, 0       ; column number
.col:
   cmp r13, [columns]
   jge .nextrow
   mov rdi, fmt_ch
   mov sil, byte [r14]
   call printf
   inc r13
   inc r14
   jmp .col
.nextrow:
   mov rdi, eoln
   call printf
   inc r12
   jmp .row
.done:
   jmp do_menu
   
   
m7:                                     ; Menu choice 7:  Write the board to a file
   cmp r15, 7
   jg bad_menu
.getname:        ; get the file name from the user
   mov rdi, prompt_file_name
   call printf
   mov rdi, fmt_str
   mov rsi, filename
   call scanf
   mov r12, rax
   call eatline
   cmp r12, 1
   je .testfile
   mov rdi, error_file_name
   call printf
   jmp .getname
.testfile:      ; don't overwrite an existing file
   mov rdi, filename
   mov rsi, 0  ; pass 0 as argument to access to test whether file exists
   call access
   cmp eax, -1  ; -1 means file does not exist; this is considered an error
   je .open_output_file
   mov rdi, error_exists
   call printf
   jmp do_menu
.open_output_file:    ; try to open the file for output
   mov rdi, filename
   mov rsi, mode_write
   call fopen
   mov r13, rax   ; r13 is the FILE address for the output file
   cmp r13, 0     ; return value 0 indicates file could not be opened for writing
   jne .writefile
   mov rdi, error_cant_write
   call printf
   jmp do_menu
.writefile:       ; file has been opened, write the board data
   mov rdi, r13   ; first, write the number of rows and columns
   mov rsi, fmt_two_int
   mov rdx, [rows]
   mov rcx, [columns]
   call fprintf
   mov rdi, r13
   mov rsi, eoln
   call fprintf
   mov r14, 0       ; row number
   mov r12, [board] ; address for reading a char from the board
.row:               ; write the characters from one row of the board
   cmp r14, [rows]
   jge .doneprint
   mov r15, 0       ; column number
.col:
   cmp r15, [columns]
   jge .nextrow
   mov rdi, r13
   mov rsi, fmt_ch_out
   mov dl, [r12]   ; dl = board[r14][r15]  (dl is lower 8 bits of rdx)
   call fprintf
   inc r15
   inc r12
   jmp .col
.nextrow:
   mov rdi, r13
   mov rsi, eoln
   call fprintf
   inc r14
   jmp .row
.doneprint:         ; write a file eoln for the last row and close the file 
   mov rdi, r13
   mov rsi, eoln
   call fprintf
   mov rdi, r13
   call fclose
   mov rdi, ok
   call printf
   jmp do_menu
   

bad_menu:             ; User's input was not a legal menu choice in the range 1 to 7
    mov rdi, error_bad_menu
    call printf
    jmp do_menu
    
;----------------------------------------------------

eatline:                 ; called after getting input from the user to discard the rest of the line
   call getchar
   cmp rax, 10
   jne eatline
   ret
      

random_fill:             ; fill the board randomly with stars and dots
   mov r12, [rows]
   imul r12, [columns]
   mov r13, 0
   mov r14, [board]
.loop:
   cmp r13, r12
   jge .done
   call rand
   and rax, 0x100
   cmp rax, 0
   je .star
   mov byte [r14 + r13], '.'
   jmp .next
.star:
   mov byte [r14 + r13], '*'
.next:
   inc r13
   jmp .loop
.done:
   ret

   
