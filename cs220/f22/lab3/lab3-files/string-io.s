# This program reads strings from the user and
# outputs a copy of each string.

# First, print a string giving the user instructions

li $1 1         # trap number for Print String
li $2 50        # Instructions are stored at location 50
lui $3 100      # maxlength for the string is 25600 -- very large to avoid discarding characters
syscall

# start of loop for reading and echoing strings.

li $1 3         # trap number for Read String
li $2 100       # String will be stored starting at location 100
lui $3 1        # maxlength for the string is 256; if user types extra characters, they are discarded
syscall

# If the value stored in location 100 is 0, then the input was empty, so jump to halt.
# We first need to read the value from memory location 100 into a register.

li $1 100       # address of memory location
lw $2 $1        # read value into register 2
beqz $2 10      # jump to halt if the value is zero

# Otherwise, output the user's string, followed by an end-of-line

li $1 1         # trap number for Print String
li $2 100       # the input string was placed at location 100
lui $3 1        # maxlength for the string is 256
syscall

li $1 1         # trap number for Print String, for outputing an end-of-line
li $2 40        # the "\n" string is at memory location 40
li $3 1         # maxlength for the string (which actually has length 1)
syscall

beqz $0 -16     # unconditional jump back to start of loop

li $1 0         #halt
syscall


@40
"\n

@50
"Enter some strings, press return to end\n


