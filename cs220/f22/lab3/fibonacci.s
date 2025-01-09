
# This program computes the fifth Fibonacci number, using a recursive subroutine.
# (The subroutine is an *extremely* inefficient way to computer Fibonacci numbers!)


lui $15 4   # initialize stack pointer

li $1 5    # Use the subroutine to compute the Fibonacci(5)
li $2 100
jalr $14 $2

add $2 $0 $1  # print the answer that was returned in register 1
li $1 2
syscall

li $1 0   # halt
syscall


#---------------------------------------------------------

@100

# Fibonacci subroutine (only works for small inputs).
#     parameter N in register 1
#     return address in register 14
#     return the Nth Fibonacci number in register 1
# This subroutine also uses registers 2 and 3 and does not preserve their values.
# For this subroutine, the 0th and 1st Fibonacci numbers are 1.

# base case, N is zero

bnez $1 2    # if N is NOT zero, jump to the next base case by skipping next three instructions
li $1 1      # if N is zero, store 1 as return value and return from subroutine
jalr $0 $14

# second base case, N is one

li $2 1        # subtract 1 from N
sub $1 $1 $2
bnez $1 2      # If N-1 is non-zero, jump to recursive case by skipping next two instructions
li $1 1        # if N is one, store 1 as return value and return from subroutine
jalr $0 $14

# recursive case, N is not zero.  We need to compute the N-1 and N-2 Fibonacci values.

# Currently, register 1 holds N-1.
# Add N-1, space for Fibonacci(N-1), and return address to the stack.

li $2 3         # make space for activation record of size 3 on the stack
sub $15 $15 $2
sw $14 $15 0    # return address is at offset 0 from top of stack
sw $1 $15 1     # N-1 is at offset 1 from top of stack
                # Fibonacci(N-1) will be at offset 2 from top of stack

li $2 100       # call the subroutine, leaving Fibonacci(N-1) in register 1
jalr $14 $2

sw $1 $15 2     # Save Fibonacci(N-1) on the stack

lw $1 $15 1     # Get N-1 from the stack into register 1

li $2 1         # decrement register 1, giving N-2
sub $1 $1 $2

li $2 100       # call the subroutine, leaving Fibonacci(N-2) in register 1
jalr $14 $2

lw $2 $15 2     # retrieve Finonacci(N-1) from the stack , into register 2

add $1 $1 $2    # add it to Fibonacci(N-2) in register 1, giving Fibonacci(N) in register 1

lw $14 $15 0    # retrieve return address from the stack

li $2 3         # restore value of stack pointer by adding 3
add $15 $15 $2

jalr $0 $14     # return from subroutine


