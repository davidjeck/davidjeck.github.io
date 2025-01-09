import textio.TextIO;

/**
 *  This program administers a ten question addtion quiz to the user.
 *  A question asks the user to add two small randomly chosen integers.
 *  The user has two tries to answer a question.  If the user gives
 *  the correct answer on the first try, the user gets 10 points.
 *  If the user gets the correct answer on the second try, the 
 *  user gets 5 points.  At the end of the quiz, the program
 *  outputs the user total score.
 */
public class AdditionQuiz {

    public static void main(String[] args) {
        
        int questionNum;  // counts the questions
        int score;  // keeps the user's score
        
        int x,y;  // the two numbers in a questions
        int correctAns;  // the correct answer, which is x+y
        int userAns;  // the user's answer
        
        questionNum = 1;
        score = 0;
        
        System.out.println();
        System.out.println("Hi!  You are about to take a quiz about adding numbers.");
        System.out.println("I will give you five addition problems.  You will have up");
        System.out.println("to two chances to answer each question.  If you give the");
        System.out.println("correct answer on the first try, you get 10 points.  If not,");
        System.out.println("you get another try, and if you give the correct ansewer on");
        System.out.println("the second try, you get 5 points.  At the end of the quiz");
        System.out.println("I will tell you how many points you got.  Let's begin!!");
        System.out.println();

        
        while (questionNum <= 10) {
        
            // Create the question.
            
            x = (int)(61*Math.random());
            y = (int)(41*Math.random());
            correctAns = x + y;
            
            // Ask the question.
            
            System.out.println("Question " + questionNum);
            System.out.printf("    What is %d + %d ?  ", x, y);
            userAns = TextIO.getlnInt();
            if ( userAns == correctAns ) { // correct on first try
                System.out.println("    That's correct!  You get 10 points.");
                score = score + 10;
            }
            else { // give the user another try
                System.out.println("    Sorry, that's not correct.  Try again!");
                System.out.printf("    What is %d + %d ?  ", x, y);
                userAns = TextIO.getlnInt();
                if (userAns == correctAns) { // correct on second try
                    System.out.println("    That's correct!  You get 5 points.");
                    score = score + 5;
                }
                else { // incorrect on second try
                    System.out.println("    Sorry, that's still not right.");
                    System.out.printf("    The correct sum is %d + %d = %d.%n", x, y, correctAns);
                }
            }
            System.out.println();
            
            questionNum = questionNum + 1;
            
        } // end while
        
        System.out.println();
        System.out.println("That's it.  Let's see how you did.");
        System.out.println("On all 10 questions, you got a total of " + score + " points");
        System.out.println();

    }

} // end class AdditionQuiz

