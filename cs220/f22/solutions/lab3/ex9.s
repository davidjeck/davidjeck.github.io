
# A program that finds the sums of two lists of numbers, using a subroutine

li $1 1         # print label for first sum
li $2 50
li $3 100
syscall

lui $1 1        # call subroutine to find sum of first list
li $2 5
li $3 100
jalr $14 $3

add $2 $0 $1    # print the return value from the subroutine
li $1 2
syscall

li $1 1         # print end-of-line
li $2 48
li $3 100
syscall

li $1 1         # print label for second sum
li $2 70
li $3 100
syscall

lui $1 2        # call subroutine to find sum of second list
li $2 25
li $3 100
jalr $14 $3

add $2 $0 $1    # print the return value from the subroutine
li $1 2
syscall

li $1 0         # halt
syscall

# strings used in the program

@48
"\n
@50
"Sum of first list: 
@70
"Sum of second list: 

#-----------------------------------------------------------------

# Subroutine that finds the sum of a list of integers, starting at location 100.
# Before calling it, the address of the start of the list must be in register 1,
# and the number of integers in the list number be in register 2.  The return
# address for the subroutine must be in register 14.  When the subroutine
# returns, the sum of the list is in register 3.  This subroutine also uses
# registers ... and does not preserve their previous values.

@100

li $11 1    # The constant 1.
li $12 0    # The sum of the list, initialized to zero.

# a loop that loads a number from the list into register 13,
# adds it to the sum, and then increments the memory address in
# register 1 and decrements the list length in register 2.  
# Stop when the list length reaches zero.

lw $13 $1
add $12 $12 $13
add $1 $1 $11
sub $2 $2 $11
bnez $2 -5

# copy the final sum to register 1, and return from subroutine
add $1 $0 $12
jalr $0 $14

#------------------------------------------------------------------

# A list of five numbers, starting at memory location 0x0100
@256
5
-3
7
10
-2


# A list of 25 numbers, starting at memory location 0x0200

@512
405
217
-30
-268
41
-11
399
1075
495
-377
6
-53
267
141
-90
40
331
471
283
34
-196
-66
70
276
-207



