/**
 * An object of type PokerCard represents a playing card from a
 * standard Poker deck, including Jokers.  The card has a suit, which
 * can be spades, hearts, diamonds, clubs, or joker.  A space, heart,
 * diamond, or club has one of the 13 values: 2, 3, 4, 5, 6, 7,
 * 8, 9, 10, jack, queen, king, or ace.  Note that "ace" is considered to be
 * the largest value.  A joker can also have an associated value; 
 * this value can be anything and can be used to keep track of several
 * different jokers.
 */

public class PokerCard {

	public final static int SPADES = 0;       // Codes for the 4 suits, plus Joker.
	public final static int HEARTS = 1;
	public final static int DIAMONDS = 2;
	public final static int CLUBS = 3;
	public final static int JOKER = 4;

	public final static int ACE = 14;        // Codes for the non-numeric cards.
	public final static int JACK = 11;        //   Cards 2 through 10 have their 
	public final static int QUEEN = 12;       //   numerical values for their codes.
	public final static int KING = 13;

	/**
	 * This card's suit, one of the constants SPADES, HEARTS, DIAMONDS,
	 * CLUBS, or JOKER.  The suit cannot be changed after the card is
	 * constructed.
	 */
	private final int suit; 

	/**
	 * The card's value.  For a normal cards, this is one of the values
	 * 2 through 14, with 14 representing ACE.  For a JOKER, the value
	 * can be anything.  The value cannot be changed after the card
	 * is constructed.
	 */
	private final int value;

	/**
	 * Creates a Joker, with 1 as the associated value.  (Note that
	 * "new PokerCard()" is equivalent to "new PokerCard(1,PokerCard.JOKER)".)
	 */
	public PokerCard() {
		suit = JOKER;
		value = 1;
	}

	/**
	 * Creates a card with a specified suit and value.
	 * @param theValue the value of the new card.  For a regular card (non-joker),
	 * the value must be in the range 2 through 14, with 14 representing an Ace.
	 * You can use the constants PokerCard.ACE, PokerCard.JACK, PokerCard.QUEEN, and PokerCard.KING.  
	 * For a Joker, the value can be anything.
	 * @param theSuit the suit of the new card.  This must be one of the values
	 * PokerCard.SPADES, PokerCard.HEARTS, PokerCard.DIAMONDS, PokerCard.CLUBS, or PokerCard.JOKER.
	 * @throws IllegalArgumentException if the parameter values are not in the
	 * Permissible ranges
	 */
	public PokerCard(int theValue, int theSuit) {
		if (theSuit != SPADES && theSuit != HEARTS && theSuit != DIAMONDS && 
				theSuit != CLUBS && theSuit != JOKER)
			throw new IllegalArgumentException("Illegal playing card suit");
		if (theSuit != JOKER && (theValue < 2 || theValue > 14))
			throw new IllegalArgumentException("Illegal playing card value");
		value = theValue;
		suit = theSuit;
	}

	/**
	 * Returns the suit of this card.
	 * @returns the suit, which is one of the constants PokerCard.SPADES, 
	 * PokerCard.HEARTS, PokerCard.DIAMONDS, PokerCard.CLUBS, or PokerCard.JOKER
	 */
	public int getSuit() {
		return suit;
	}

	/**
	 * Returns the value of this card.
	 * @return the value, which is one the numbers 2 through 14, inclusive for
	 * a regular card, and which can be any value for a Joker.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns a String representation of the card's suit.
	 * @return one of the strings "Spades", "Hearts", "Diamonds", "Clubs"
	 * or "Joker".
	 */
	public String getSuitAsString() {
		switch ( suit ) {
		case SPADES:   return "Spades";
		case HEARTS:   return "Hearts";
		case DIAMONDS: return "Diamonds";
		case CLUBS:    return "Clubs";
		default:       return "Joker";
		}
	}

	/**
	 * Returns a String representation of the card's value.
	 * @return for a regular card, one of the strings "2",
	 * "3", ..., "10", "Jack", "Queen", "King" or "Ace".  For a Joker, the 
	 * string is always numerical.
	 */
	public String getValueAsString() {
		if (suit == JOKER)
			return "" + value;
		else {
			switch ( value ) {
			case 2:   return "2";
			case 3:   return "3";
			case 4:   return "4";
			case 5:   return "5";
			case 6:   return "6";
			case 7:   return "7";
			case 8:   return "8";
			case 9:   return "9";
			case 10:  return "10";
			case 11:  return "Jack";
			case 12:  return "Queen";
			case 13:  return "King";
			default: return "Ace";
			}
		}
	}

	/**
	 * Returns a string representation of this card, including both
	 * its suit and its value (except that for a Joker with value 1,
	 * the return value is just "Joker").  Sample return values
	 * are: "Queen of Hearts", "10 of Diamonds", "Ace of Spades",
	 * "Joker", "Joker #2"
	 */
	public String toString() {
		if (suit == JOKER) {
			if (value == 1)
				return "Joker";
			else
				return "Joker #" + value;
		}
		else
			return getValueAsString() + " of " + getSuitAsString();
	}

	/**
	 * Checks whether this card equals another card.  Cards are equal
	 * if they have the same suit and value.
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PokerCard))
			return false;
		PokerCard that = (PokerCard)obj;
		return (this.suit == that.suit && this.value == that.value);
	}

} // end class PokerCard
