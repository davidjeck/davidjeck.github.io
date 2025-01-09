
/**
 * An object of type ArithmeticProblem represents an addition,
 * subtraction, multiplication, or division problem using small
 * integer operands.  There are public methods for getting the
 * question and the correct answer, and a constructor that makes
 * a random question.
 */
public class ArithmeticProblem {
	
	private int a, b;       // The two operands for the problem.
	private char operator;  // The operator, '+', '-', '*', or '/'.
	
	/**
	 * This constructor creates a random arithmetic problem.
	 */
	public ArithmeticProblem() {
		switch ((int)(Math.random() * 4)) {
			case 0 -> { // addition problem, with sum less than 100
				operator = '+';
				a = (int)(50*Math.random() + 1);
				b = (int)(50*Math.random());
			}
			case 1 -> { // subtraction problem, with non-negative answer
				operator = '-';
				do {
					a = (int)(100*Math.random());
					b = (int)(100*Math.random());
				} while (a < b);
			}
			case 2 -> { // multiplication problem, with operands 0 to 12
				operator = '*';
				a = (int)(13*Math.random());
				b = (int)(13*Math.random());
			}
			default -> { // division problem, with integer answer
				operator = '/';
				int answer = (int)(25*Math.random());
				b = (int)(1 + 12*Math.random());
				a = b*answer;
			}
		}
	}
	
	/**
	 * Return a string that asks this question, including a question mark at the end.
	 */
	public String getQuestion() {
		return String.format("What is %d %c %d ? ", a, operator, b);
	}
	
	/**
	 * Return the correct answer for this question.
	 */
	public int getAnswer() {
		switch (operator) {
			case '+': return a + b;
			case '-': return a - b;
			case '*': return a * b;
			default : return a / b;
		}
	}

} // end class ArithmeticProblem
