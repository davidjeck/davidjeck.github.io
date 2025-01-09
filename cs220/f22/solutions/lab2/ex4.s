
# This program loads two numbers, x and y, into registers 1 and 2.
# It computes x raised to the power y, and outputs the answer.
# (x is 3 and y is 5, but the program will work for any x and y.) 

li $1 3       # place input values into registers 1 and 2
li $2 5

li $3 1       # register 1 is used to compute the product, starting from 1
li $4 1       # register 4 holds the constant 1

beqz $2 3     # if y is 1, then there is nothing to do; skip the loop

# start of loop

mul $3 $3 $1  # multiply the value in register 3 by x
sub $2 $2 $4  # subtract 1 from y
bnez $2 -3    # if y is not yet zero, jump back to start of loop

# end of loop

add $2 $0 $3  # copy the answer to register 2

li $1 2       # output the answer
syscall

li $1 0       # halt
syscall

