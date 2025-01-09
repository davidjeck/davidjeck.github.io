# This program reads a list of integers from the user
# and stores the integers in consecutive locations in
# memory, starting at location 100.  The program ends
# when the input number is zero.

li $1 1           # output a line of instructions
li $2 18
li $3 100
syscall

li $4 100         # Address where input value is to be stored.
li $5 1           # The constant, 1, for incrementing the address.

# Start of loop: Input a number and store it in memory.

li $1 1           # Print the prompt, >>>
li $2 60
li $3 10
syscall

li $1 4           # Read integer into register 1
syscall

sw $1 $4          # Store integer at address stored in register 4

beqz $1 2         # If the input number is zero, jump to end

add $4 $4 $5      # Increment the address in register 4

beqz $0 -10       # Unconditional jump back to start of loop

li $0 1           # Halt
syscall

# Strings for I/O (first one at location 18, second one at location 60)
"Enter some integers.  Enter zero to end.\n
">>> 

