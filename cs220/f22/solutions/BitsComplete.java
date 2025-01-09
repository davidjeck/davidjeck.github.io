
import java.util.Scanner;

/**
 * This program reads 32-bit integers from the user in decimal, binary, or hexidecimal
 * form.  It also accepts values of type float, but interprets the bits in a float as
 * a 32-bit int.  It then outputs the same bits interpreted as a binary, hexadecimal,
 * and decimal integer, and also as a float.
 */
public class BitsComplete {
	
	private final static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

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
			        // If you are unfamiliar with the multiline string syntax that
			        // is used above, check out the new text blocks in Java 17
			        // at https://math.hws.edu/javanotes/c2/s3.html#basics.3.3a
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
		if (str.length() == 2)
			throw new IllegalArgumentException("Illegal hex string; nothing found after the 0x.");
		long n = 0;
		for (int i = 2; i < str.length(); i++) {
			char ch = str.charAt(i);
			int d = switch(ch) {
				case '0' -> 0;
				case '1' -> 1;
				case '2' -> 2;                 // If you are unfamiliar with this syntax,
				case '3' -> 3;                 // check out the new "switch expression"
				case '4' -> 4;                 // at https://math.hws.edu/javanotes/c3/s6.html
				case '5' -> 5;
				case '6' -> 6;
				case '7' -> 7;
				case '8' -> 8;
				case '9' -> 9;
				case 'a' -> 10;
				case 'b' -> 11;
				case 'c' -> 12;
				case 'd' -> 13;
				case 'e' -> 14;
				case 'f' -> 15;
				default -> throw new IllegalArgumentException("Illegal hex string; " + ch + " is not a hex digit.");
			};
			n = (n << 4) | d;
			if (n > 4294967295L)
				throw new IllegalArgumentException("Hexadecimal number out of range; max is 0xffffffff.");
		}
		return (int)n;
	}
	

	/**
	 * Take a lower-case string that starts with "0b" and try to interpret the
	 * rest of the string as the binary representation of a 32-bit int.
	 * If the rest of the string does not consists of one to thirty-two 
	 * zeros and ones, then an IllegalArgumentException is thrown.
	 */
	private static int binaryStringToInt(String str) {
		if (str.length() == 2)
			throw new IllegalArgumentException("Illegal hex string; nothing found after the 0b.");
		long n = 0;
		for (int i = 2; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '0')
				n = n << 1;
			else if (ch == '1')
				n = (n << 1) | 1;
			else
				throw new IllegalArgumentException("Illegal binary string; " + ch + " is not a binary digit.");
		}
		if (n > 4294967295L)
			throw new IllegalArgumentException("Binary number out of range; max is 0x11111111111111111111111111111111.");
		return (int)n;
	}

	
	/**
	 * Take a string and try to parse it as the decimal (base 10) representation of
	 * a 32-bit integer.  The string can optionally start with a plus or minus sign.
	 * If the string does not represent a valid int, then an IllegalArgumentException
	 * is thrown.
	 */
	private static int decimalStringToInt(String str) {
		boolean negative = false;
		if (str.startsWith("+"))
			str = str.substring(1);
		else if (str.startsWith("-")) {
			str = str.substring(1);
			negative = true;
		}
		long n = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch < '0' || ch > '9')
				throw new IllegalArgumentException("Illegal decimal value; " + ch + " is not a decimal digit.");
			int d = ch - '0';
			n = 10*n + d;
			if (n > 4294967295L)
				throw new IllegalArgumentException("Decimal value out of range; maximum is 4294967295.");
		}
		if (negative) {
			if (n > 2147483648L)
				throw new IllegalArgumentException("Negative decimal value out of range; minimum is -2147483648.");
			n = -n;
		}
		return (int)n;
	}

	
	/**
	 * Construct the binary string representation of n (starting with "0b").
	 */
	private static String intToBinaryString(int n) {
		String s = "";
		do {
			if ( (n & 1) == 0)
				s = '0' + s;
			else
				s = '1' + s;
			n = n >>> 1;
		} while (n != 0);
		return "0b" + s;
	}

	
	/**
	 * Construct the hexadecimal string representation of n (starting with "0x").
	 */
	private static String intToHexString(int n) {
		String s = "";
		do {
			int d = n & 0xf;
			s = hexDigits[d] + s;
			n = n >>> 4;
		} while (n != 0);
		return "0x" + s;
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
	       if ( n == 0x7f800000 )
	           return Float.POSITIVE_INFINITY;
	       if ( n == 0xff800000 )
	           return Float.NEGATIVE_INFINITY;
	       if ( (n >= 0x7f800001 && n <= 0x7fffffff) || (n >= 0xff800001 && n <= 0xffffffff ) )
	           return Float.NaN;
	       int s = ((n >> 31) == 0) ? 1 : -1;
	       int e = ((n >> 23) & 0xff);
	       int m = (e == 0) ?
	                 (n & 0x7fffff) << 1 :
	                 (n & 0x7fffff) | 0x800000;
	       //System.out.printf("                             s=%d, e=%d, m=%d%n",s,e,m);
	       return (float)(s * m * Math.pow(2.0,e-150));
	}


} // end class BitsComplete

