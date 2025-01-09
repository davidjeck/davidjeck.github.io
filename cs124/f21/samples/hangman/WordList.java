import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * An object of type WordList reads a list of words from a resource file
 * that is part of the same program and makes the words available.
 */
public class WordList {
	
	private ArrayList<String> words;  // the list of words.
	
	/**
	 * Create a list of words by reading from a resource file.
	 * @param wordlistFileName the path to the resource file in the program.
	 * @throws IllegalArgumentException if words can't be read from the resource file.
	 */
	public WordList(String wordlistFileName) {
		words = new ArrayList<String>();
		try {
			Scanner in;
			in = new Scanner(getClass().getClassLoader().getResource(wordlistFileName).openStream());
			while (in.hasNextLine()) {
				String line = in.nextLine().trim();
				if (line.length() > 0)
					words.add(line);
			}
			if (words.size() == 0)
				throw new IllegalArgumentException("Nothing was found in the file " + wordlistFileName);
			System.out.println(words.size() + " words read from " + wordlistFileName);
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Can't read list of words from " + wordlistFileName);
		}
	}
	
	/**
	 * Returns the number of words in the list.
	 */
	public int getWordCount() {
		return words.size();
	}
	
	/**
	 * Returns a specified word from the list.
	 * @param index the position of the word in the list, with positions numbered from 0 to getWordCount() - 1.
	 * @throws IndexOutOfBoundsException if index is not in the range 0 to getWordCount() - 1.
	 */
	public String getWord(int index) {
		return words.get(index);
	}
	
	/**
	 * Returns a specified word from the list, and removes that word from the list so it can't
	 * be fetched again.  The size of the list decreases by one.
	 * @param index the position of the word in the list, with positions numbered from 0 to getWordCount() - 1.
	 * @throws IndexOutOfBoundsException if index is not in the range 0 to getWordCount() - 1.
	 */
	public String removeWord(int index) {
		return words.remove(index);
	}

    /**
     * Returns a randomly selected word from the list of words, and removes it from 
     * the list so that it will not be returned again.
     */	
	public String removeRandomWord() {
	    int index = (int)(getWordCount()*Math.random());
	    return removeWord(index);
	}

}
