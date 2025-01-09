# Puts 0xFACE into register 10

lui $1 0xFA    # Put 0xFA00 in regiser 1

lui $2 0xCE    # Put 0xCE00 in register 2

li $3 8        # shift regster 2 right by 8 bits, giving 0x00CE
srl $2 $2 $3

add $10 $1 $2  # final result is 0xFACE in register 10

li $1 0        # halt
syscall
