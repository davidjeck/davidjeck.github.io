
# Call the subroutine to compute 48 % 5

li $1 48  # A
li $2 5   # B
li $3 100 # Address of subroutine
jalr $0 $15

# Now, $1 contains the answer

add $2 $1 $0  # copy answer to register 2
li $1 2       # syscal number for print integer
syscall       # print the answer

li $1 0 
syscall  # HALT




# Subroutine to computer A % B.
# Inputs:  A in Register 1, B in Register 2
# Output:  A % B in Register 1, computed as A - (A/B)*B
# Return address:  Must be saved in regiser 15
# Uses: Register 3

@100  # Subroutine starts at location 100

div $3 $1 $2    #  S = A/B
mul $3 $3 $2    #  R = R * B = (A/B)*B
sub $1 $1 $3    #  A = A - R = A - (A/B)*B
jalr $0 $15
