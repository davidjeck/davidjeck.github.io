
/**
 * This program simulates playing 10,000,000 games of Craps.
 * It reports the fraction of games that were won.
 * (When run, the program reports a fraction won of about 0.493.)
 */
public class CrapsProbability {

    public static void main(String[] args) {
        int wins;  // Number of times the game is won.
        wins = 0;
        for (int i = 0; i < 10000000; i++) {
            if (playCraps()) {
                wins++;
            }
        }
        System.out.printf("%nThe fraction of craps games won was %1.5f%n%n",
                             wins/10000000.0);
    }
    
    /**
     * Simulate rolling a pair of ordinary, six-sided dice.
     * Return the total of the numbers showing on the two dice.
     */
    private static int roll() {
        int d1, d2;
        d1 = (int)(6*Math.random() + 1);
        d2 = (int)(6*Math.random() + 1);
        return d1 + d2;
    }
    
    /**
     * Simulate one game of Craps.  Return true if the game is won, and
     * return false if the game is lost.  In Craps, a pair of dice is
     * rolled if the total on the dice is 7 or 11, it's a win.  If the
     * total is 2, 3, or 12, it's a loss.  If the total is anything else,
     * then the dice are rolled until either the same total is gotten
     * again (which is called "making point"), or the total is seven.
     * A 7 means the game is lost, which making point means the game is won.
     */
    private static boolean playCraps() {
        int point;  // The total showing on the dice (in the first roll).    
        point = roll();
        if (point == 7 || point == 11) {
            return true;  // Won the game on the first roll.
        }
        else if (point == 2 || point == 3 || point == 12) {
            return false;  // Lost the game on the first roll.
        }
        else { // Roll the dice until point recurs or a 7 comes up
            while (true) {
                int diceTotal;  // total showing on dice in next roll
                diceTotal = roll();
                if (diceTotal == point) {
                    return true; // Won by getting "making point."
                }
                else if (diceTotal == 7) {
                    return false;  // Lost by getting a 7 before making point.
                }
            }
        }
    }

}

