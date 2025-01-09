#include <stdio.h>

/* This C program is meant to test the assembly language functions written
 * for Part A of Lab 11.  To use it, lab11A.asm must first be assembled using
 *       yasm -felf64 -gdwarf2 lab11A.asm
 * Then, it is linked to this file using the gcc compiler/linker:
 *       gcc -g -o lab11 lab11main.c lab11A.o
 * The program can then be run using ./lab11.
 *
 * (Note that depending on how you write the assembly functions, it might
 * be necessary to add the -no-pie option to the gcc command:
 *       gcc -g -no-pie -o lab11 lab11main.c lab11A.o
 * If -no-pie is needed, gcc will give you an error message mentioning PIE.)
 */

extern long sum8( long a, long b, long c, long d, long e, long f, long g, long h );
extern long searchArray( long A[], long len, long x );
extern unsigned long fibonacci(unsigned long N);


long list1[] = { 23, 98, -33, 10, -34, 2 }; // an array of length 6
long list2[] = { 211,718,62,229,445,137,73,215,790,355,801,375,100,299,
                 574,476,94,753,695,830,412,77,108,82,534,630,34,21,636,
                 394,377,356,535,620,182,655,238
               };  // an array of length 37

int main() {

   printf("sum8(10,-3,7,20,-15,2,15,6) = %li\n\n", sum8(10,-3,7,20,-15,2,15,6));

   long result; // return value from searchArray()
   result = searchArray(list1, 6, 10); // look for an item that's in the short array
   printf("Is  10 in list1?  %s\n", result == -1 ? "no" : "yes");
   result = searchArray(list1, 6, 42); // look for item that's not there
   printf("Is  42 in list1?  %s\n", result == -1 ? "no" : "yes");
   result = searchArray(list2, 37, 636); // look for an item that's in the long array
   printf("Is 636 in list2?  %s\n", result == -1 ? "no" : "yes");
   result = searchArray(list2, 37, -17); // look for item that's not there
   printf("Is -17 in list2?  %s\n", result == -1 ? "no" : "yes");
   result = searchArray(list2, 37, 211); // look for first item in array
   printf("Is 211 in list2?  %s\n", result == -1 ? "no" : "yes");
   result = searchArray(list2, 37, 238); // look for last item in array
   printf("Is 238 in list2?  %s\n\n", result == -1 ? "no" : "yes");
   
   for (unsigned long N = 0; N < 10; N++) {
      printf("Fibonacci(%lu) is %lu\n", N, fibonacci(N));
   }

}

