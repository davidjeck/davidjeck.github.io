
import java.util.Scanner;

/**
 * This program reads 32-bit integers from the user in decimal, binary, or hexidecimal
 * form.  It also accepts values of type float, but interprets the bits in a float as
 * a 32-bit int.  It then outputs the same bits interpreted as a binary, hexadecimal,
 * and decimal integer, and also as a float.
 */
public class Bits {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println();
			System.out.print("Enter a number, press return to end: ");
			String line = in.nextLine().toLowerCase().trim();
			if (line.length() == 0)
				break;
			int n;  // The user's input, as a 32-bit int value.
			try {
				if (line.startsWith("0x")) {
					System.out.println("Interpreting input as a hexadecimal integer.");
					n = hexStringToInt(line);
				}
				else if (line.startsWith("0b")) {
					System.out.println("Interpreting input as a binary integer.");
					n = binaryStringToInt(line);
				}
				else if (line.contains(".") || line.contains("e")) {
					System.out.println("Interpreting input as a float.");
					n = floatStringToInt(line);
				}
				else {
					System.out.println("Interpreting input as a decimal integer.");
					n = decimalStringToInt(line);
				}
			}
			catch (IllegalArgumentException e) {
				System.out.println("Illegal input: " + e.getMessage());
				continue;
			}
			String binary = intToBinaryString(n); // Represent n as a binary string.
			String hex = intToHexString(n);       // Represent n as a hexadecimal string.
			float real = myIntBitsToFloat(n);     // Interpret the bits in n as an IEEE 754 32-bit float.
			System.out.println();
			System.out.printf("""
					The bits in that value interpreted as...
					    decimal integer:     %d
					    binary integer:      %s
					    hexadecimal integer: %s
					    converted to float:  %g
					""", n, binary, hex, real);
			System.out.println();
			System.out.printf("""
					For comparison, here are Java's built-in versions:
					    binary integer:      0b%s
					    hexadecimal integer: 0x%s
					    converted to float:  %g
					""", Integer.toBinaryString(n), Integer.toHexString(n), Float.intBitsToFloat(n));
			System.out.println();
		} // end while
		in.close();
	} // end main()

	
	/**
	 * Parse a string as a value of type float, then return the int value
	 * with the same bit representation as the float.  (This really just
	 * interprets the exact same bits as an int instead of as a float.)
	 */
	private static int floatStringToInt(String str) {
		float x;
		try {
			x = Float.parseFloat(str);
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException(str + " is not a legal float");
		}
		return Float.floatToRawIntBits(x);  // Let java do the type conversion!
	}

	
	/**
	 * Take a lower-case string that starts with "0x" and try to interpret the
	 * rest of the string as the hexadecimal representation of a 32-bit int.
	 * If the rest of the string does not consists of one to eight hexadecimal
	 * digits, then an IllegalArgumentException is thrown.
	 */
	private static int hexStringToInt(String str) {
		return 0;
	}
	

	/**
	 * Take a lower-case string that starts with "0b" and try to interpret the
	 * rest of the string as the binary representation of a 32-bit int.
	 * If the rest of the string does not consists of one to thirty-two 
	 * zeros and ones, then an IllegalArgumentException is thrown.
	 */
	private static int binaryStringToInt(String str) {
		return 0;
	}

	
	/**
	 * Take a string and try to parse it as the decimal (base 10) representation of
	 * a 32-bit integer.  The string can optionally start with a plus or minus sign.
	 * If the string does not represent a valid int, then an IllegalArgumentException
	 * is thrown.
	 */
	private static int decimalStringToInt(String str) {
		return 0;
	}

	
	/**
	 * Construct the binary string representation of n (starting with "0b").
	 */
	private static String intToBinaryString(int n) {
		return "";
	}

	
	/**
	 * Construct the hexadecimal string representation of n (starting with "0x").
	 */
	private static String intToHexString(int n) {
		return "";
	}

	
	/**
	 * Find the value of type float that has the same bit representation as n.
	 * Note that the return value can be Float.NaN, Float.POSITIVE_INFINITY,
	 * or Float.NEGATIVE_INFINITY, as well as an ordinary, finite number.
	 * All NaN values can be returned as Float.NaN.
	 * The exact algorithm for the conversion is given in the documentation for Float.intBitsToFloat:
	 * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Float.html#intBitsToFloat(int)
	 */
	private static float myIntBitsToFloat(int n) {
		return 0;
	}


} // end class Bits
