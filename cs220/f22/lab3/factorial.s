
# This program computes factorial of 6, using a recursive subroutine.
# (This program will only give correct answers for N <= 8.)


lui $15 4   # initialize stack pointer

li $1 6     # Use the subroutine (at address 100) to compute factorial of 6
li $2 100
jalr $14 $2

add $2 $0 $1  # print the answer that was returned in register 1
li $1 2
syscall

li $1 0   # halt
syscall


#---------------------------------------------------------

@100

# factorial subroutine (only works for N from 0 to 8).
#     parameter N in register 1
#     return address in register 14
#     return value N! in register 1
# this subroutine also uses register 2 and does not preserve its value

bnez $1 2    # if N is NOT zero, jump to the recursive case by skipping next two instructions

# base case, N is zero (no activation record is needed in this case)

li $1 1      # if N is zero, store 1 as return value and return from subroutine
jalr $0 $14

# recursive case, N is not zero

# Add an activation record contaiing N and return address to the stack.

li $2 2           # size of the activation record
sub $15 $15 $2
sw $1 $15 0       # store N at offset 0 from the top of the stack
sw $14 $15 1      # store return address at offset 1

# decrement N, in register 1, and call the subroutine, leaving (N-1) factorial in register 1

li $2 1
sub $1 $1 $2
li $2 100
jalr $14 $2

# get N and the stack, into register 2, and multiply register 1 by N

lw $2 $15 0      # retrieve N from the stack, at offset 0, into register 2
mul $1 $1 $2     # register 1 now contains the return value, factorial(N)

# retrieve the return address from the stack, delete the activation record, and return

lw $14 $15 1     # retrieve return address from the stack, at offset 1

li $2 2          # restrore stack pointer by adding 2
add $15 $15 $2

jalr $0 $14      # return

