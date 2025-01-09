
// Note:  This version of the solution to the Baseball exercise uses only
// things that had been covered at the time of the lab.  See Baseball.java
// for a solution that uses boolean operators to simplify the program.


/**
 * This program simulates a game of baseball between the Yankees and the
 * Red Sox.  This is a very simple simulation in which the number of runs
 * scored by each team in each inning is generated randomly.
 */
public class BaseballBasic {
    
    public static void main( String[] args ) {
        
        int yankeesTotal;  // Number of runs scored by the Yankees.
        int redsoxTotal;   // Number of runs scored by the Red Sox.
        int inning;   // Current inning number.
        int yankeesRuns, redsoxRuns; // Runs scored in current inning.
        
        inning = 1;
        yankeesTotal = 0;
        redsoxTotal = 0;
        
        while (inning <= 9) {

            System.out.println();
            System.out.println("Inning number " + inning);

            /* The method used here for generating a random number of runs
             * will generate an integer in the range 0 to 6, with 0 being
             * the most common value and the probability decreasing as the
             * number of runs increases.  This is done by raising an random
             * number between 0 and 1 to the tenth power.  Raising a number
             * between 0 and 1 to a power makes that number smaller, which
             * makes it more likely to be close to zero. */
            
            yankeesRuns = (int)( 7*Math.pow(Math.random(),10) );
            System.out.println("   The Yankees score " + yankeesRuns + " runs");
            yankeesTotal = yankeesTotal + yankeesRuns;
            
            if (inning == 9) { // Last inning -- Red sox might not need to bat
                if (redsoxTotal > yankeesTotal) {
                    System.out.println(
                         "   In the 9th inning, the Red Sox are ahead, so do not bat.");
                }
                else { // Red Sox bat in the bottom of the 9th.
                    redsoxRuns = (int)( 7*Math.pow(Math.random(),10) );
                    System.out.println("   The Red Sox score " + redsoxRuns + " runs");
                    redsoxTotal = redsoxTotal + redsoxRuns;
                }
            }
            else { // innings 1 through 8
                redsoxRuns = (int)( 7*Math.pow(Math.random(),10) );
                System.out.println("   The Red Sox score " + redsoxRuns + " runs");
                redsoxTotal = redsoxTotal + redsoxRuns;
            }
                        
            System.out.println("The total score is now Yankees " + yankeesTotal 
                                       + " to Red Sox " + redsoxTotal);
            inning = inning + 1;
            
        } // end 9 innings
        
        if ( redsoxTotal == yankeesTotal ) {
            System.out.println();
            System.out.println("The score is tied so the game goes into extra innings!");
        }
        
        while (redsoxTotal == yankeesTotal) { // continue game since score is tied
            System.out.println();
            System.out.println("Inning number " + inning);
            
            yankeesRuns = (int)( 7*Math.pow(Math.random(),10) );
            System.out.println("   The Yankees score " + yankeesRuns + " runs");
            yankeesTotal = yankeesTotal + yankeesRuns;
        
            redsoxRuns = (int)( 7*Math.pow(Math.random(),10) );
            System.out.println("   The Red Sox score " + redsoxRuns + " runs");
            redsoxTotal = redsoxTotal + redsoxRuns;
            
            System.out.println("The total score is now Yankees " + yankeesTotal 
                                       + " to Red Sox " + redsoxTotal);                     
            inning = inning + 1;
        } // end extra innings
        
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

