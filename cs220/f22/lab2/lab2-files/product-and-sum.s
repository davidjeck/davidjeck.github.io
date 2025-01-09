
# This program reads two ints from the user and outputs their sum
# and their product.  Note that it does not prompt the user for input,
# and it does not print new-lines after the outputs.

li $1 4        # "read int" syscall, to read the first input number
syscall

add $2 $0 $1   # copy the first input integer into register 2

li $1 4        # for reading the second input number
syscall

add $3 $0 $1   # copy the second input integer into register 3

add $4 $2 $3   # compute the sum of the two inputs
mul $5 $2 $3   # compute the product of the two inputs

li $1 2        # "print int" syscall
add $2 $0 $4   # put the sum into register 2, which holds the value to be printed
syscall

li $1 2        # "print int" syscall
add $2 $0 $5   # put the product into register 2, which holds the value to be printed
syscall

li $1 0        # "halt" syscall
syscall


