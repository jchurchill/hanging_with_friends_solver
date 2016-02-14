package hwf.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class WordFinder {

	Dictionary dict = new Dictionary();
	String[] dictionaryFiles = {"/resource/CROSSWD.TXT", "/resource/CRSWD-D.TXT"};
	
	public WordFinder() throws IllegalArgumentException, IOException {
		initializeDictionary();
	}
	
	public ArrayList<String> makeQuery(String query, String excluding, String letterTray, boolean enforceVowelRule) throws Exception {		
		return findWords(query, excluding, letterTray, enforceVowelRule);
	}
	
	private void initializeDictionary() throws IllegalArgumentException, IOException {
		for(String f : dictionaryFiles) {
			Dictionary.makeDictionary(dict, f);
		}
	}
	
	private ArrayList<String> findWords(String query, String excluding, String letterTray, boolean enforceVowelRule) throws Exception {
		if(query == null) {
			query = "";
		}
		if(letterTray == null) {
			letterTray = "";
		}
		if(excluding == null) {
			excluding = "";
		}
		
		StringBuilder letters = new StringBuilder();
		letters.append(letterTray);
		if(query != null && query.length() > 0) {
			query = hwfQuery(query, letters, enforceVowelRule);
		}
		ArrayList<String> result = dict.wordsWithLetters(letters.toString());
		result = filter(result, query, excluding);
		Collections.sort(result, new StrLenComparator());
		return result;
	}
	
	private static ArrayList<String> filter(ArrayList<String> words, String regex, String excluding) {
		excluding = excluding.toUpperCase();
		ArrayList<String> result = new ArrayList<String>();
		for(String word : words) {
			if(regex == null || regex.equals("") || Pattern.matches(regex, word.toUpperCase())) {
				boolean add = true;
				for(int i=0; i<word.length(); i++) {
					if(excluding.indexOf(Character.toUpperCase(word.charAt(i))) != -1) {
						add = false;
						break;
					}
				}
				if(add) {
					result.add(word);
				}
			}
		}
		return result;
	}
	
	/*
	 * Accepts a string containing '.'s representing unknowns and upper case letters
	 * representing knowns
	 */
	private static String hwfQuery(String query, StringBuilder letters, boolean enforceVowelRule) throws Exception {
		int rightmostVowelIndex = -1;
		String charsUsed = "";
		String resultQuery = "";
		for(int i=0; i<query.length(); i++) {
			if(Character.isLetter(query.charAt(i))) {
				char letter = Character.toUpperCase(query.charAt(i));
				letters.append(letter);
				if (letter == 'A' ||
					letter == 'E' ||
					letter == 'I' ||
					letter == 'O' ||
					letter == 'U') {
					rightmostVowelIndex = i;
				}
				if(charsUsed.indexOf(letter) == -1) {
					charsUsed += letter;
				}
			}
			else if(query.charAt(i) == '.') {
				letters.append('*');
			}
			else {
				// Not a letter or a '.' - must be bad syntax
				throw new Exception("Argument query has bad syntax: " + query);
			}
		}
		for(int i=0; i<query.length(); i++) {
			if(i > rightmostVowelIndex && enforceVowelRule) {
				for(int j=0; j<5; j++) {
					char letter = "AEIOU".charAt(j);
					if(charsUsed.indexOf(letter) == -1) {
						charsUsed += letter;
					}
				}
			}
			if(Character.isLetter(query.charAt(i))) {
				resultQuery += Character.toUpperCase(query.charAt(i));
			}
			else {
				if(charsUsed.length() > 0) {
					resultQuery += ("[^" + charsUsed + "]");
				}
				else {
					resultQuery += '.';
				}
			}
		}
		return resultQuery;
	}
	
	public static int[] letterFrequency(ArrayList<String> words) {
		int[] freqs = new int[26];
		for(String word : words) {
			for(int i=0; i<word.length(); i++) {
				char c = Character.toUpperCase(word.charAt(i));
				// Only count the letter once per word
				if(i == word.indexOf(c)) {
					freqs[c - 'A']++;
				}
			}
		}
		return freqs;
	}
}

class StrLenComparator implements Comparator<String> {

	@Override
	public int compare(String arg0, String arg1) {
		return arg1.length() - arg0.length();
	}
}

