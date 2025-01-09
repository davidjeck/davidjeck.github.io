import textio.TextIO;

/**
 * When the computer executes this program, it holds a simple
 * conversation with the user.
 */
 
 public class Conversation {

    public static void main(String[] args) {
    
       String name;  // the name of the user of this program.
       
        System.out.println("Hi!  I'm a computer!  What's your name? ");
        name = TextIO.getln();
        System.out.println("Pleased to meet you, " + name);
        
    } // end main()
 
 } // end class Conversation
 
