; A program to make a copy of a file, inefficiently, one byte at a time.
; The user of the program is asked to specify file names (which must not
; contain spaces!).  If the input file does not exist, the program ends
; immediately.  The program will refuse to copy the file if the output
; file already exists.  If that happens, or if the output file cannot
; be opened, the program ends immediately.

global _start
extern fopen, fclose, access, printf, scanf, fgetc, fprintf

section .rodata

    prompt1 db "Input file:  ", 0
    prompt2 db "Output file: ", 0
    
    mode_read db "r", 0    ; mode arguments for fopen
    mode_write db "w", 0
    
    read_fmt db "%s", 0    ; for reading filename from user
    write_fmt db "%c", 0   ; for writing one character to output file
    
    error_fmt db "Exiting because an error occurred: %s", 0
    error_read db "Cannot open file for input.", 10, 0
    error_exists db "Output file exists.", 10, 0
    error_write db "Cannot open file for output.", 10, 0
    error_copy db "Sorry, some error occurred while copying the file.", 10, 0

section .bss

    filename resb 256  ; Allow file paths up to 255 characters, without error checking.
    
section .text

_start:  ; Get input file name from user, and open the file.
    
    mov rdi, prompt1
    call printf

    mov rdi, read_fmt
    mov rsi, filename
    call scanf
    
    mov rdi, filename
    mov rsi, mode_read
    call fopen
    mov r12, rax   ; r12 is the FILE address for the input file
    
    cmp r12, 0     ; return value 0 indicates file could not be opened for reading
    jne get_output_file
    mov rdi, error_read
    call error ; (does not return)
    
get_output_file:         ; get output file name from user, and check if file exists
    mov rdi, read_fmt
    mov rdi, prompt2
    call printf

    mov rdi, read_fmt
    mov rsi, filename
    call scanf
    
    mov rdi, filename
    mov rsi, 0  ; pass 0 as argument to access to test whether file exists
    call access
    cmp eax, -1  ; -1 means file does not exist; this is considered an error
    je open_output_file
    mov rdi, error_exists
    call error ; (does not return)

open_output_file:       ; open file for output
    mov rdi, filename
    mov rsi, mode_write
    call fopen
    mov r13, rax   ; r13 is the FILE address for the input file
    
    cmp r13, 0     ; return value 0 indicates file could not be opened for writing
    jne copy_file
    mov rdi, error_write
    call error ; (does not return)
    
copy_file:         ; read a char from input file, write it to output file
    mov rdi, r12
    call fgetc
    cmp eax, -1     ; occurs at end-of-file
    je close_files
    mov rdi, r13
    mov rsi, write_fmt
    mov rdx, rax
    call fprintf
    cmp rax, 0    ; value 0 indicates error becasue the character was not written
    jne copy_file
    mov rdi, error_copy
    call error  ; (does not return)
    
close_files:      ; close the files; might be necessary ensure all output is written
    mov rdi, r12
    call fclose
    mov rdi, r13
    call fclose
    
    mov rax, 60  ; EXIT with success becuase file has (probably) been copied
    mov rdi, 0
    syscall
    
;-----------
; Function error(msg): exit with error; argument is the address of the error message.
error:  ; 
   mov rsi, rdi   ; move message address to rsi register to be 2nd argument for printf
   mov rdi, error_fmt  ; output format for printf, to print error message
   call printf
   mov rax, 60    ; EXIT; note that this function does not return
   mov rdi, 1     ; non-zero argument to exit syscall means "exit with error"
   syscall
    
