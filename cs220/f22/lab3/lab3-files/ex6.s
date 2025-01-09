
# This program reads two ints from the user and outputs the max of
# the two inputs.  Note that it does not prompt the user for input.

li $1 4        # "read int" syscall, to read the first input number
syscall

add $2 $0 $1   # copy the first input integer into register 2

li $1 4        # for reading the second input number
syscall

add $3 $0 $1   # copy the second input integer into register 3

slt $4 $2 $3   # reg 4 is 0 if reg 2 is less than reg 3, or is 1 otherwise.
beqz $4 1      # skip next instruction if reg 2 is the max

add $2 $0 $3   # copy reg 3 to reg 2; reg 2 now contains the larger value

li $1 2        # print the number in reg 2
syscall

li $1 0        # halt
syscall

