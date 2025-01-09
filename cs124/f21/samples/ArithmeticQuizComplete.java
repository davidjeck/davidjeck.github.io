
import java.util.Scanner;

/**
 * Administer a random arithmetic quiz to the user.  The quiz contains 
 * 10 problems, and the user gets 10 points for each correct answer.  
 * The user's total score is reported at the end.
 */
public class ArithmeticQuizComplete {

    public static void main(String[] args) {
        
        Scanner in = new Scanner( System.in );  // For reading user input.
        int userAnswer; // The user's answer to one of the problems;        
        int score = 0;  // The user's score on the quiz.
        
        for (int questionNum = 1; questionNum <= 10; questionNum++) {
            ArithmeticProblem question = new ArithmeticProblem();
            System.out.println();
            System.out.print("Question " + questionNum + ": " + question.getQuestion());
            while (true) {
                try {
                    String answerString = in.nextLine().trim();
                    userAnswer = Integer.parseInt(answerString);
                    break;
                }
                catch (NumberFormatException e) {
                    System.out.print("   Please enter an integer as your answer: ");
                }
            }
            if (userAnswer == question.getAnswer()) {
                System.out.println("   Yes, " + userAnswer + " is correct!");
                score += 10;
            }
            else {
                System.out.println("   Sorry, the correct answer is " + question.getAnswer());
            }
        }
        
        System.out.println();
        System.out.println("Your score on the quiz is " + score);
                
    }
    

}
