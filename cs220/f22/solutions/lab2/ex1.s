
# Compute 7*256-25*70, leaving the answer in register 1.

li $2 7            # load constants into registers 2 through 5
lui $3 1
li $4 25
li $5 70

mul $2 $2 $3      # Reg[2] = 7*256
mul $4 $4 $5      # Reg[4] = 25*70
sub $10 $2 $4     # final answer in register 10

li $1 0           # halt
syscall

