
; This program reads a data file named data12.txt, does some computation with the data,
; and writes the results to an output file named result12.txt.  

global _start
extern fopen, fclose, fprintf,  fscanf, printf

section .rodata

    
    mode_read db "r", 0    ; mode arguments for fopen
    mode_write db "w", 0
    
    input_file_name db "data12.txt", 0
    output_file_name db "result12.txt", 0
    
    fmt_str_int db "%s %ld", 0   ; for readingthe string and an int at the start of an input line
    fmt_float db "%lg", 0        ; for reading a loating point number from input
    fmt_out db "%s %lg", 10, 0   ; format for writing one line of output containing a name and average
    
    error_fmt db "Exiting because an error occurred: %s", 0
    error_read db "Cannot open file data12.txt for input.", 10, 0
    error_write db "Cannot open file result12.txt for output.", 10, 0
    error_process db "Sorry, some error occurred while processing the file.", 10, 0
    

section .bss
     
    name resb 100  ; The name that is the first item on an input line.
    count resq 1   ; The integer that is the second item on an input line.
    data resq 1    ; One of the floating point data items from an input line.
    sum resq 1     ; The sum of the floating point data items from an input line.
    
section .text

_start:  

; open the input file.
        
    mov rdi, input_file_name
    mov rsi, mode_read
    call fopen
    mov r12, rax   ; r12 is the FILE address for the input file
    
    cmp r12, 0     ; return value 0 indicates file could not be opened for reading
    jne open_output_file
    mov rdi, error_read
    call error ; (does not return)
    
; open a file for output

open_output_file:       
    mov rdi, output_file_name
    mov rsi, mode_write
    call fopen
    mov r13, rax   ; r13 is the FILE address for the input file
    
    cmp r13, 0     ; return value 0 indicates file could not be opened for writing
    jne process_line
    mov rdi, error_write
    call error ; (does not return)
    
; Read one line of input and write a corresponding result line to the output file.
    
process_line:
       ; r12:   input file handle
       ; r13:   output file handle
       ; r15:   counter
       ; name:  the name at the start of the line
       ; count: number of data items on line
       ; data:  one data value from the input line
       ; sum:   the sum of the data items

    mov rdi, r12      ; read the name and integer at the start of the line
    mov rsi, fmt_str_int
    mov rdx, name
    mov rcx, count
    call fscanf
    cmp rax, 2        ; if read failed, take it as a signal of end of file
    jne close_files
        
    ; Read the data items and compute the sum in xmm0
    
    mov r15, 0           ; for counting the number of data items read so far
    mov qword [sum], 0   ; sum = 0; note: integer 0 and float 0.0 have the same binary represention (all zeros)
  next_data:
    cmp r15, [count]     ; if r15 = count, we have read all the data from the line
    je output
    mov rdi, r12         ; read one item into data
    mov rsi, fmt_float
    mov rdx, data
    call fscanf
    cmp rax, 1           ; if the return value is not 1, there was a read error; abort
    jne bad
    movsd xmm0, [sum]    ; sum = sum + data
    addsd xmm0, [data]
    movsd [sum], xmm0
    inc r15
    jmp next_data
    
    ; compute the average and write the name and average to a line of output
    
  output:
    cvtsi2sd xmm1, qword [count] ; convert integer count to floating point, for the division
    movsd xmm0, [sum]     ; do xmm0 = sum / count
    divsd xmm0, xmm1      ; divide total by count to get the average
    mov rax, 1            ; for fprintf, number of xmm registers used in this read
    mov rdi, r13          ; now do the output
    mov rsi, fmt_out
    mov rdx, name
    ; xmm0 already contains the average
    call fprintf
    jmp process_line
    
bad:  ; There was an error reading one of the data items, indicating file is bad.
    mov rdi, error_process
    call error ; (does not return)
    
close_files:      ; close the files; might be necessary ensure all output is written
    mov rdi, r12
    call fclose
    mov rdi, r13
    call fclose
    
    mov rax, 60  ; EXIT with success becuase file has (probably) been processed completely
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
    
