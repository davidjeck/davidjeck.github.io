
# This program reads an int from the user, counts the number of 1's
# in the input value, and outputs the answer.

li $1 4        # "read int" syscall, to read the user's input number, N
syscall

li $2 0        # Load a 0 into register 2, which will be used for counting the 1's in N

beqz $1 9      # if N is zero, there is nothing to do, so jump to outputting the answer, which will be 0

li $3 1        # load the constant 1 into register 3, which will be used as a shift amount
li $4 15       # load the constant 15 into register 4, which will be used as a shift amount


# start of loop

add $5 $0 $1   # copy N to register 5
sll $5 $5 $4   # shift left by 15, then right by 15, leaving the rightmost bit of N in  register 5
srl $5 $5 $4

beqz $5 1      # if the right bit of N is zero, skip the next instruction
add $2 $2 $3   # otherwise the right bit of N is 1, so increment the counter

srl $1 $1 $3   # shift N one bit to the right
bnez $1 -7     # if N is not zero, jump back to start of loop

# answer is in register 2

li $1 2        # print value in register 2
syscall

li $1 0        # halt
syscall



