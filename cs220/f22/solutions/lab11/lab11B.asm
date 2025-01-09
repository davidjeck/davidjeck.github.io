
; The computer picks a number in the range 1 to 100 and the user tries to guess it.
;
; This program can be assembled and linked using my build script:
;
;    /classes/cs220/build.sh -c lab11B-complete.asm
;
; The "-c" option is required since the program uses functions from
; the C standard library.  To assemble and link it by had, use for example
; 
;    yasm -felf64 -gdwarf2 lab11B-complete.asm
;    ld -g -o guess lab11B-complete.o -lc --dynamic-linker /lib/x86_64-linux-gnu/ld-linux-x86-64.so.2

global _start
extern printf, scanf, getchar, time, rand, srand

section .data

  greet db "I picked a number in the range 1 to 100.  Try to guess it.", 10, 10, 0
  prompt db "What is your guess? ", 0
  output_bad db "Please enter an integer!", 10, 0
  output_won db "You got it in %d tries.", 10, 0
  output_lost1 db "Sorry, you used up your 6 guesses.", 10, 0
  output_lost2 db "My number was %i.", 10, 0
  output_high db "Too high!  Try again.", 10, 0
  output_low db "Too low!  Try again.", 10, 0
  input_fmt db "%i"
  
section .bss
  
  userGuess resd 1
  
section .text

_start:

    mov rdi, 0    ; initialize the random number generator
    call time
    mov rdi, rax
    call srand

    mov rdi, greet  ; Tell user about the game
    call printf
    
    call rand       ; get a random number, into rax
    mov edx, 0      ; divide by 100 and move remainder into r12, then add 1
    mov ebx, 100
    div ebx
    mov r12d, edx
    inc r12d         ; r12d is the computer's number, in the range 1 to 100
    
    mov r13d, 0      ; r13d is the number of guesses that the user has made
    
nextguess:
    inc r13d        ; guessCount++
    mov rdi, prompt ; prompt the user for input
    call printf
    mov rdi, input_fmt
    mov rsi, userGuess
    call scanf      ; read user's input number into [userGuess]
    cmp rax, 1      ; if return value is not 1, input is bad
    je testcorrect
        ; tell user input is bad and discard the rest of the input line
    dec r13d        ; don't count this guess
  nextch:
    call getchar
    cmp rax,10
    jne nextch
    jmp nextguess
    
testcorrect:
    cmp r12d, [userGuess]  ; if the userGuess is equal to computer's number, the user wins
    jne testlost
    mov rdi, output_won  ; tell user they won
    mov esi, r13d
    call printf
    jmp gameover
   
testlost:
    cmp r13d, 6           ; if the user has used 6 guesses, the user loses
    jne testhigh
    mov rdi, output_lost1
    call printf
    mov rdi, output_lost2
    mov esi, r12d
    call printf
    jmp gameover

testhigh:
    cmp r12d, [userGuess] ; if the user's guess is too high, tell the user and go to next guess
    jg low
    mov rdi, output_high
    call printf
    jmp nextguess

low:
    mov rdi, output_low ; finally, we know the guess is too low; tell the user and go to next guess
    call printf
    jmp nextguess    


gameover:
    mov rax, 60  ; EXIT
    mov rdi, 0
    syscall
    
