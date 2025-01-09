li $1 3    # Test load
lui $2 2   # Test load upper
li $3 0xC9 # Test sign extension of LIMM 

# Test arithmetic
add $4 $1 $2
sub $5 $1 $2
mul $6 $1 $2
div $7 $2 $1
sll $8 $2 $1
srl $9 $2 $1
nor $10 $1 $2
slt $11 $1 $2

# Test memory
li $12 0  # location
li $13 17  # data
sw $13 $12  # 17 into location 0
lw $14 $12  # 17 from location 0 to register 14
li $15 2  # base address
sw $14 $15 1 # test SIMM, 17 into location 3
sw $14 $15 -1 # test SIMM sign extension, 17 into location 1

# Test branching -- store 3,2,1 into locations 4,5,6
li $12 1
li $13 3
li $14 4
beqz $13 4
sw $13 $14
sub $13 $13 $12
add $14 $14 $12
beqz $0 -5
bnez $12 1 # skip next instruction
sw $12 $14 # value in location 7 should still be 0
li $0 42   # should not change value of register 0

# next instruction is in location 29
# test jalr
li $13 32
jalr $12 $13 # 30 into register 12, jump past next instruction
li $12 0

# store all register values into locations 8, 9, ...
li $15 8
li $14 1
sw $0 $15
add $15 $15 $14
sw $1 $15
add $15 $15 $14
sw $2 $15
add $15 $15 $14
sw $3 $15
add $15 $15 $14
sw $4 $15
add $15 $15 $14
sw $5 $15
add $15 $15 $14
sw $6 $15
add $15 $15 $14
sw $7 $15
add $15 $15 $14
sw $8 $15
add $15 $15 $14
sw $9 $15
add $15 $15 $14
sw $10 $15
add $15 $15 $14
sw $11 $15
add $15 $15 $14
sw $12 $15
add $15 $15 $14
sw $13 $15
add $15 $15 $14
sw $14 $15
add $15 $15 $14
sw $15 $15

#test halt -- any syscall should halt the computer
li $1 0
syscall







