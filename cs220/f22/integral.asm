; This program computes an approximation for the area under the curve y = sin(x)
; from 0 to pi.  The area is approximated by 10,000 rectangles.  The exact value 
; of the integral is 2.  (Note: link this program with the C standard library!)

extern atan, sin, printf

section .rodata

    outputfmt  db  "My approximation for the area is: %1.15g", 10, 0

    ; for convenience, some floating point constants; one way to get
    ; a number into a floating point register it to read it from memory.
    
    four: dq 4.0
    tenthou: dq 10000.0

section .text

global _start
_start:

 
   ; Algorithm:
   ;     area = 0.0       ; for summing the areas of the rectangles
   ;     dx = pi/10000.0  ; the width of the rectangles
   ;     for ( i = 0; i < 10000; i++ ) {
   ;         x = dx * i   ; value on the x axis, randing from 0 to pi
   ;         height = sin(x)   ; function value at x gives height of rectangle
   ;         area + height*dx  ; add height of this rectangle to the area
   ;      }
   ;      output Area

   ; data assignments:
   ;    xmm10:  Area, used to sum up the areas of the rectangles
   ;    xmm11:  dx, the width of a rectangle, equal to pi/10000.0
   ;    ebx:    i, counter for the rectangles
   
   ; As an example, compute pi as 4.0*atan(1.0).
   mov r10d, 1          ; another way to get a constant into an xmm register
   cvtsi2sd xmm0, r10d  ; xmm0 = 1.0
   call atan            ; puts atan(1.0) in register xmm0
   movsd xmm11, xmm0    ; xmm11 is now atan(1.0, which is pi/4
   movsd xmm0, [four]   ; another way to get a constant into an xmm register
   mulsd xmm11, xmm0    ; xmm11 *= 4.0; now xmm0 contains 4*atan(1), which is pi
   movsd xmm0, [tenthou]
   divsd xmm11, xmm0    ; xmm11 /= 10000.0 now xmm0 contains dx = pi/10000.0
   
   ; Now compute the sum of the 10000 rectangles, answer into xmm10
   mov r10d, 0
   cvtsi2sd xmm10, r10d ; Area = 0
   
   mov ebx, 0           ; i = 0
sum:
   cvtsi2sd xmm0, ebx   ; xmm0 = i
   mulsd xmm0, xmm11    ; xmm0 = dx*i 
   call sin             ; xmm0 = height = sin(dx*i)
   mulsd xmm0, xmm11    ; xmm0 = height*dx
   addsd xmm10, xmm0    ; area = xmm10 = area + height*dx
   inc ebx              ; i = i + 1
   cmp ebx, 10000
   jl sum

   
   ; The value for the area that we want to output is in xmm10.  To be used
   ; as the first floating point argument to printf, it must be copied into xmm0.
   
   mov rax, 1           ; For vararg functions like printf and fprintf, rax should be the
                        ; number of xmm registers that are used for passing arguments!
                        ; (But apparently it only really needs to be non-zero.)
                        
   mov rdi, outputfmt
   movsd xmm0, xmm10
   call printf

   
   mov rax, 60  ; EXIT
   mov rdi, 0
   syscall

