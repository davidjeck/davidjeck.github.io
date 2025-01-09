
/**
 *  Find the first 10000 prime numbers.  An integer, N,  is prime if N is
 *  greater than one and there is no smaller positive integer, other than 1,
 *  that evenly divides N.  In fact, it is enough to test that there is no
 *  prime number less than or equal to the square root of N that evenly
 *  divides N.  The program tests each number 2, 3, 4, ..., to see whether
 *  it is prime.  When it finds a prime number, it adds it to an array.
 *  When testing N, all the primes that are less than N are already in
 *  the array, so only numbers from the array have to be checked as
 *  possible divisors divisors.  Since the numbers in the array are in
 *  increasing order, we can stop testing if we get to a number from 
 *  the array that is greater than the square root of N.
 */
public class PrimesWithSubroutines {
       
   private static int[] primes;    // To be filled with the first 10000 primes.
   private static int primeCt;     // Number of primes found so far.
       
   public static void main(String[] args) {
   
       long startTime, endTime;  // For timing the computation.
   
       startTime = System.nanoTime();
       
       findPrimes(10000);
       
       endTime = System.nanoTime();
       
       printPrimes();
       
       System.out.printf( "%n%nThe computation took %1.5f seconds.%n%n",
                               (endTime - startTime) / 1000000000.0 );
   
   } // end main()
   
   
   /**
    * Find the first numToFind prime numbers, and store them in the primes array.
    * Creates the array, of length numToFind, and fills it with primes.
    */
   private static void findPrimes( int numToFind ) {
   
       int candidate;   // A number to be tested to see if it's prime.
   
       primes = new int[numToFind];
       primeCt = 0;
       
       candidate = 2;  // First possible prime.
       while (primeCt < numToFind) {
           if ( isPrime(candidate) ) {
               newPrime(candidate);
           }
           candidate++;
       }
   }
       
       
   /**
    *  Test whether the number N is a prime.  (All prime numbers less than
    *  or equal to the square root of N must already in the primes array!)
    */
   private static boolean isPrime( int N ) {
   
       int index;   // An index into the primes array.
       int limit;   // Numbers bigger than this can't be divisors.
   
       limit = (int)Math.sqrt( N + 0.01 ); 
                   // The +0.01 is to be safe from calculation errors, like calculating
                   // Math.sqrt(49) and getting 6.999999999999.
       
       index = 0; // Start testing divisors with the first number in the array.
       
       while (index < primeCt && primes[index] <= limit) { // Test next number from the array as a possible divisor
           if ( N % primes[index] == 0) {
               return false;  // We found a divisor; N is not prime.
           }
           index++; // Go on to the next possible divisor from the array.
       }
       
       return true;  // We didn't find a divisor, so N is prime.
       
   } // end isPrime();
   
   
   /**
    *  Add N to the array of prime numbers.
    */
   private static void newPrime( int N ) {
       primes[primeCt] = N;
       primeCt++;
   }
   
   
   /**
    *  Print all the numbers from the primes arrays, ten to a line, in neat columns.
    */
   private static void printPrimes() {
       if (primes.length > 10000) {
           System.out.println("The array is too big to print!");
           return;  // Refuse to print very large arrray, in case the number is modified in main().
       }
       for (int i = 0; i < primes.length; i += 10) {
           for (int j = i; j < i+10; j++) {
               System.out.printf("%7d", primes[j]);
           }
           System.out.println();
       }
   }
       
} // end class          
      

