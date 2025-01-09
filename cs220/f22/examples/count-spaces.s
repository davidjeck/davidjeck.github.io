li $1 3       # read a string, store at location 256
lui $2 1
li $3 100
syscall

lui $1 1      # call subroutine, passing address of string,
li $2 100
jalr $15 $2

# register 1 now holds the number of spaces in the string

add $2 $1 $0  # print the answer
li $1 2
syscall

li $1 0       # halt
syscall


@100 # subroutine to count spaces in a string, address of string in $1;
     # uses registers 1 through 5, and register 15 for return address;
     # return value in register 1

li $4 32      # code for space char
li $5 1       # constant 1

add $2 $1 $0  # address now in register 2
li $1 0       # counter

# start of loop
lw $3 $2      # read char into register 3
beqz $3 5     # if char is zero, done 
sub $3 $3 $4
bnez $3 1     # if char is not 32, skip next instruction
add $1 $1 $5  # increment counter
add $2 $2 $5  # increment address
beqz $0 -7    # back to start of loop

jalr $0 $15   # return
