
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
public class Primes10000 {

   public static void main(String[] args) {
       
       int[] primes;    // To be filled with the first 10000 primes.
       int primeCt;     // Number of primes found so far.
       int candidate;   // A number to be tested to see if it's prime.
       boolean isPrime; // For testing if the candidate is prime.
       int index;       // An index into the primes array.
       int divisor;     // A possible divisor of the candidate; one of the primes already found.
       int limit;       // Numbers bigger than this can't be divisors.
       int i,j;         // for-loop variables.
       
       long startTime, endTime;  // For timing the computation.
       
       startTime = System.nanoTime();
       
       primes = new int[10000];
       primeCt = 0;
       
       candidate = 2;  // Start with 2.  The first number that can be prime is 2.
       
       while (primeCt < 10000) {
       
           // Test if the candidate is prime.  If so, add it to the array.
       
           isPrime = true;  // Assume that candidate is prime, unless we find a divisor.
           
           limit = (int)Math.sqrt(candidate + 0.01); 
                       // The +0.01 is to be safe from calculation errors, like calculating
                       // Math.sqrt(49) and getting 6.999999999999.
           
           index = 0; // Start testing divisors with the first number in the array.
           
           while (index < primeCt) { // Test next number from the array as a possible divisor
               divisor = primes[index];  // Get the next possible divisor from the list of primes.
               if (divisor > limit) {
                   break;  // We have tested all the possible divisors less than or equal to the limit.
               }
               if ( candidate % divisor == 0) {
                   isPrime = false;  // We found a divisor; candidate is not prime.
                   break;
               }
               index++; // Go on to the next possible divisor from the array.
           }
           
           if (isPrime) {  // Add candidate to the list of primes.
               primes[ primeCt ] = candidate;
               primeCt++;  // We have one more prime, so count it.
           }
           
           candidate++;  // Go on to the next possible prime.
       
       }
       
       endTime = System.nanoTime();
       
       System.out.println("The first 10,000 primes:");
       System.out.println();
       
       // Print the primes in neat columns, ten to a line.
       
       for (i = 0; i < 10000; i += 10) {
           for (j = i; j < i+10; j++) {
               System.out.printf("%7d", primes[j]);
           }
           System.out.println();
       }
            
       System.out.printf("%n%nThe computation took %1.5f seconds%n%n", (endTime - startTime) / 1000000000.0);

   } // end main()

} // end class Primes10000

