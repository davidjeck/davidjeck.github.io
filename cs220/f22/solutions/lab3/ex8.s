# This program reads a string from the user,
# prints the length of the string,
# then prints the reverse of the string.

li $1 1     # Prompt the user for input, by printing the string at memory location 60
li $2 60
li $3 100
syscall 

li $1 3     # syscall number for reading an input string
lui $2 1    # string will be stored at location 256
lui $3 100  # max length is very large, 25600, to avoid cutting off the string
syscall

# Note that at this time, Register 3 contains the actual length of the string, plus 1

li $10 1    # The constant 1, to be subtracted from register 6.
sub $6 $3 $10  # Register 6 now contains the length of the string.

# Print the length of the string, followed by end-of-line.

li $1 2     # print int from location 6, which is the string length
add $2 $0 $6
syscall
li $1 1     # print end of line, from memory location 50
li $2 50
li $3 1
syscall

# Now, print the reverse of the string, and halt.
    
lui $5 1      # Memory address, initialized to 256, the start of the string.
add $5 $5 $6  # Add length of string to address, so it now points to the zero at the end of the string.

# Loop to print individual chars from the string in reverse order.
# In each iteration, decrement register 6 and the memory address. Stop if register 6 is zero;
# since register 6 originally contains the length of the string, that will be true when 
# all of the characters in the string have been output.  (Note that the memory address
# is decremented before printing anything, to account for the fact that it initially points
# to the zero at the end of the string.)

beqz $6 7      # If register 6 is zero, we are done, jump out of the loop.
sub $5 $5 $10  # decrement memory address
sub $6 $6 $10  # decrement counter
li $1 1        # print individual char from memory address in register 5
add $2 $0 $5
li $3 1
syscall
beqz $0 -8     # jump back to start of loop

li $1 0        # Halt
syscall

@50
"\n

@60
"Enter a string. I will output the string length and the reverse of the string.\n

