# Computes the 3N+1 sequence starting from 3 and counts the
# number of terms in the sequence.  The terms of the 
# sequence appear in Register 5, and Register 6 is used
# for counting.

li $4 3  # Initialize register 5 with the first term, N, of the sequence.
li $5 1  # Initialize counting register with one, which counts the initial term.

li $1 1  # Store the constants used in the calculation in registers 1, 2, and 3.
li $2 3
li $3 15


add $10 $0 $4   # Copy N into register 10
sub $10 $10 $1  # Subtract 1 from (copy of) N
beqz $10 10     # If result is zero, the sequence is done, jump to end.

add $5 $5 $1    # Add 1 to register 6, to count this step in the sequence

add $10 $0 $4   # Copy N into register 10.

sll $10 $10 $3  # Set register 10 to the low-order bit in N.
srl $10 $10 $3  #     so register 10 will be non-zero when N is odd.

bnez $10 2      # If N is odd, jump ahead by 2 (skipping next two instructions.

srl $4 $4 $1    # Divide N by 2.
beqz $0 -10     # Unconditional jump back 10 instructions, to start of loop.

mul $4 $4 $2    # Multiply N by 3.
add $4 $4 $1    # Add 1 to N.
beqz $0 -13     # Unconditional jump back 13 instructions, to start of loop.

li $1 0         # These two instructions will halt the computer.
syscall



