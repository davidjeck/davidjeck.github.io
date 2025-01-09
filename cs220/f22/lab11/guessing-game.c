#include <time.h>
#include <stdio.h>
#include <stdlib.h>

/* Guessing game.  Computer picks a random number in the range 1 to 100.
 * The user tries to guess it.  The computer tells the user if their guess
 * is high or low.  The user has 6 tries to guess the number.
 */

int main() {

    // initialize the random number generator with the current time.
    unsigned long now = time(0);
    srand(now);
    
    int N = (rand() % 100) + 1; // random number in the range 1 to 100
    
    printf("I picked a number in the range 1 to 100.  Try to guess it.\n\n");
    
    int guessCount = 0;  // Count the user's guesses.
    int userGuess;       // The user's guess;
    
    while (1) {
        guessCount++;
        printf("What is your guess? ");
        int i = scanf("%i", &userGuess);
        if ( i != 1 ) {
            printf("Please enter an integer!\n");
            guessCount--; // don't count this guess
            int ch;
            do { // eat the rest of the input line
               ch = getchar();
            } while (ch != 10);
        }
        else if (userGuess == N) {
           printf("You got it in %d tries.\n", guessCount);
           break;
        }
        else if (guessCount == 6) {
           printf("Sorry, you used up your 6 guesses.\n");
           printf("My number was %i.\n", N);
           break;
        }
        else if (userGuess > N) {
            printf("Too high!  Try again.");
        }
        else {
            printf("Too low!  Try again.");
        }
    }

}
