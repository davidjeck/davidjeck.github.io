# Computes the 3N+1 sequence starting from a number entered by the user
# and outputs the terms of the sequence.  (Note:  The output is not valid
# if any term of the sequence goes out of the range of 16-bit values.)

# Prompt user for input, and read response
li $1 1   # Print string at location 100
li $2 100
li $3 100
syscall
li $1 4   # Read an int (into register 1)
syscall

# Initial value of N is in register 1.

add $4 $0 $1   # Copy N to register 4

# Start of loop:  print N, exit if N is 1, compute next N
# (Note: N is saved in register 4.)

li $1 2       # trap number for print int, for printing N
add $2 $0 $4  # copy N to register 2
syscall
li $1 1       # print an end of line (from location 90)
li $2 90
li $3 1
syscall

add $1 $0 $4    # copy N to register 1
li $2 1
sub $1 $1 $2    # subtract 1 from (copy of) N
bnez $1 2       # if result is not 0, N is not 1, so skip next two instructions
li $1 0         # else halt
syscall

add $1 $0 $4    # copy N to register 1
lui $2 1        # Address of start of subroutine, 256, in register 2
jalr $14 $2     # call subroutine

add $4 $0 $1    # Copy new value of N to register 4
beqz $0 -18     # Unconditional jump back to start of loop.

# A string containing only an end-of-line (character number 10), at location 90
@90
10  # Represents \n
0   # Marks end of string

# Prompt for the user's input, at location 100
@100
"Enter the starting value of N: 

#---------------------------------------------------------------------------
# A subroutine to compute the next term in a 3N+1 sequence.  The subroutine
# is at location 256.  When it is called, the current value of N must be
# in register 1.  The subroutine should be called with jalr, saving the
# return address in register 14.  When it returns, the next value of N is 
# in register 1.   Note that this subroutine also uses registers 10, 11, 12, 
# and 13.  Values in in those registers (as well as register 14) before the 
# subroutine is called are not preserved.

@256
li $11 1        # Store the constants used in the calculation
li $12 3
li $13 15

add $10 $0 $1   # Copy N into register 10

sll $10 $10 $13 # Set register 10 to the low-order bit in N.
srl $10 $10 $13 #     so register 10 will be non-zero when N is odd.

bnez $10 2      # If N is odd, jump to the "odd" case.

srl $1 $1 $11   # N is even; divide N by 2.
jalr $0 $14     # Return from subroutine.

mul $1 $1 $12   # N is odd; multiply N by 3, then add 1
add $1 $1 $11
jalr $0 $14     # Return from subroutine.
#---------------------------------------------------------------------------




