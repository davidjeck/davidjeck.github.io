// Note:  This version of the solution to the Baseball exercise uses some
// things that were not covered at the time of the lab.  See BaseballBasi.java
// for a solution that uses only things that had been covered before the lab.


/**
 * This program simulates a game of baseball between the Yankees and the
 * Red Sox.  This is a very simple simulation in which the number of runs
 * scored by each team in each inning is generated randomly.
 */
public class Baseball {
    
    public static void main( String[] args ) {
        
        int yankeesTotal;  // Number of runs scored by the Yankees.
        int redsoxTotal;   // Number of runs scored by the Red Sox.
        int inning;   // Current inning number.
        int yankeesRuns, redsoxRuns; // Runs scored in the current inning.
                    
        inning = 1;
        yankeesTotal = 0;
        redsoxTotal = 0;
        
        while (inning <= 9 || redsoxTotal == yankeesTotal) {

            System.out.println();
            System.out.println("Inning number " + inning);

            /* The method used here for generating a random number of runs
             * will generate an integer in the range 0 to 6, with 0 being
             * the most common value and the probability decreasing as the
             * number of runs increases.  This is done by raising a random
             * number between 0 and 1 to the tenth power.  Raising a number
             * between 0 and 1 to a power makes that number smaller, which
             * makes it more likely to be close to zero. */
            
            yankeesRuns = (int)( 7*Math.pow(Math.random(),10) );
            System.out.println("   The Yankees score " + yankeesRuns + " runs");
            yankeesTotal += yankeesRuns;
            
            if (inning >= 9 && redsoxTotal > yankeesTotal) {
                    System.out.println(
                         "   In the 9th inning, the Red Sox are ahead, so do not bat.");
            }
            else {
                redsoxRuns = (int)( 7*Math.pow(Math.random(),10) );
                System.out.println("   The Red Sox score " + redsoxRuns + " runs");
                redsoxTotal += redsoxRuns;
            }
            
            System.out.println("The total score is now Yankees " + yankeesTotal 
                         + " to Red Sox " + redsoxTotal);

            if (inning == 9 && redsoxTotal == yankeesTotal) {
                System.out.println();
                System.out.println("The score is tied, so the game goes into extra innings!");
            }

            inning++;

        } // end while
        
        System.out.println();
        System.out.println();
        System.out.println("The final score is Yankees " + yankeesTotal 
                                 + " to Red Sox " + redsoxTotal);
        System.out.println();
        if (yankeesTotal > redsoxTotal)
            System.out.println("The Yankees win.");
        else
            System.out.println("The Red Sox win.");
    } 

} // end class Baseball
