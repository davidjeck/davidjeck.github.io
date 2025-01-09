
# This program reads two ints from the user and outputs their sum
# and their product.

li $1 1        # print first prompt
li $2 50
li $3 100
syscall

li $1 4        # "read int" syscall, to read the first input number
syscall

add $2 $0 $1   # copy the first input integer into register 2


li $1 1        # print second prompt
li $2 70
li $3 100
syscall

li $1 4        # for reading the second input number
syscall

add $3 $0 $1   # copy the second input integer into register 3


add $4 $2 $3   # compute the sum of the two inputs
mul $5 $2 $3   # compute the product of the two inputs


li $1 1        # label for first output
li $2 100
li $3 100
syscall

li $1 2        # "print int" syscall
add $2 $0 $4   # put the sum into register 2, which holds the value to be printed
syscall

li $1 1        # print an end-of-line
li $2 40
li $3 1
syscall


li $1 1        # label for second output
li $2 120
li $3 100
syscall

li $1 2        # "print int" syscall
add $2 $0 $5   # put the product into register 2, which holds the value to be printed
syscall


li $1 0        # "halt" syscall
syscall



# end-of-line string, at location 40
@40
10
0

# strings for I/O

@50
"Your first number: 
 
@70
"Your second number: 

@100
"The sum is 

@120
"The product is 



