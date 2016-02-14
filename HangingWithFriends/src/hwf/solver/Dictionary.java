package hwf.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class Dictionary {
	private DictionaryNode root = new DictionaryNode();
	private int wordCount = 0;
	
	public int addWord(String word) throws IllegalArgumentException {
		DictionaryNode dn = root;
		for(int i=0; i<word.length(); i++) {
			// Throw out characters which aren't letters, raise an alert
			if(!Character.isLetter(word.charAt(i))) {
				System.out.println("Warning: \"" + word + "\" added to dictionary with non-letter character \"" + word.charAt(i) + "\" removed.");
			}
			else {
				if(i == word.length()-1) {
					try {
						dn.addLastLetter(word.charAt(i));
					}
					catch (Exception e) {
						System.out.println("Word \"" + word + "\" attemped to be added to dictionary twice, was rejected.");
					}
				}
				else {
					dn = dn.addLetter(word.charAt(i));
				}
			}
		}
		return wordCount++;
	}
	
	public ArrayList<String> wordsWithLetters(String letters) {
		// The "special" character '*' indicates a wildcard, any letter
		return wordsWithLettersWildcard1(letters, root, "");
	}
	
	private ArrayList<String> wordsWithLettersWildcard1(String letters, DictionaryNode node, String wordSoFar) {
		ArrayList<String> matches = new ArrayList<String>();
		HashSet<Character> lettersSeen = new HashSet<Character>();
		String avail = letters.toUpperCase();
		// Perform a depth-first search on the dictionary using recursion.
		// If node is marked as end of word, then the wordSoFar is a match.
		if(node.isEndOfWord()) {
			matches.add(wordSoFar);
		}
		for(int i=0; i<avail.length(); i++) {
			char choice = avail.charAt(i);
				// Don't look at repeat letters (same path down trie)
				if(!lettersSeen.contains(choice)) {
					lettersSeen.add(choice);
					String nowAvail = avail.substring(0,i) + avail.substring(i+1);
					if(choice == '*') {
						for(choice = 'A'; choice <= 'Z'; choice++) {
							DictionaryNode nextNode = node.getNextLetter(choice);
							// Base case: no match for chosen letter
							// Recursive case: match exists for chosen letter
							if(nextNode != null) {
								matches.addAll(wordsWithLettersWildcard1(nowAvail, nextNode, wordSoFar+choice));
							}
						}
					}
					else {
						DictionaryNode nextNode = node.getNextLetter(choice);
						// Base case: no match for chosen letter
						// Recursive case: match exists for chosen letter
						if(nextNode != null) {
							matches.addAll(wordsWithLettersWildcard1(nowAvail, nextNode, wordSoFar+choice));
						}
					}
				}
		}
		return matches;
	}
	
	public static Dictionary makeDictionary(Dictionary dict, String dictfilename) throws IllegalArgumentException, IOException {
		long timetaken = System.currentTimeMillis();
		System.out.println("Filling dictionary with " + dictfilename + "...");
		String word;
		InputStream is = dict.getClass().getResourceAsStream(dictfilename);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while((word = br.readLine()) != null) {
			dict.addWord(word);
		}
		timetaken = System.currentTimeMillis() - timetaken;
		System.out.println(String.format("Dictionary filled. Time: %.3f s", (double)timetaken/1000));
		return dict;
	}
}
