import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;




public class build_lm {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String file = args[0];
		Map<String, Integer> unigramCount = new HashMap<String, Integer>();
		Map<String, Integer> bigramCount = new HashMap<String, Integer>();
		Map<String, Integer> trigramCount = new HashMap<String, Integer>();
		buildCount(file, unigramCount, bigramCount, trigramCount);
		
		System.out.println("\\data\\");
		System.out.println("ngram 1: type="+unigramCount.keySet().size()+" token="+getTokenCount(unigramCount));
		System.out.println("ngram 2: type="+bigramCount.keySet().size()+" token="+getTokenCount(bigramCount));
		System.out.println("ngram 3: type="+trigramCount.keySet().size()+" token="+getTokenCount(trigramCount));
		System.out.println();
		System.out.println("\\1-grams:");
		printUnigramResults(unigramCount);
		System.out.println();
		System.out.println("\\2-grams:");
		printNonUnigramResults(unigramCount, bigramCount);
		System.out.println();
		System.out.println("\\1-grams:");
		printNonUnigramResults(bigramCount, trigramCount);
		System.out.println();
		System.out.println("\\end\\");
	}
	
	private static int getTokenCount(Map<String, Integer> gramCount) {
		Iterator<String> i = gramCount.keySet().iterator();
		int ret = 0;
		while(i.hasNext()) {
			String key = i.next();
			ret += gramCount.get(key);
		}
		return ret;
	}
	
	private static void printOneLine(int count, int total, String word) {
		double prob = (double) count/total;
		double lgprob = Math.log10(prob);
		System.out.println(count+" "+prob+" "+lgprob+" "+word);
	}
	
	private static void printUnigramResults(Map<String, Integer> unigramCount) {
		int tokenCount = unigramCount.keySet().size();
		/*
		for(String curWord : unigramCount.keySet()) {
			int wordCount = unigramCount.get(curWord);
			printOneLine(wordCount, tokenCount, curWord);
		}
		*/
		Object[] result = Helper.toOrder(unigramCount);
		Object[] freqs = (Object[])result[0];
		@SuppressWarnings("unchecked")
		Map<Integer, List<String>> freqToWords = (Map<Integer, List<String>>)result[1];
		for(int i = freqs.length-1; i >= 0; i--) {
			List<String> curWords = freqToWords.get((Integer)freqs[i]);
			for(String curWord : curWords) {
				printOneLine((Integer)freqs[i], tokenCount, curWord);
			}
		}
	}
	

	
	private static void printNonUnigramResults(Map<String, Integer> smallGram, Map<String, Integer> bigGram) {
		/*
		for(String curWord : bigGram.keySet()) {
			int bigCount = bigGram.get(curWord);
			String smallGramWord = null;
			String[] bigGramTokens = curWord.split(" ");
			if(bigGramTokens.length == 3) {
				smallGramWord = bigGramTokens[0]+" "+bigGramTokens[1];
			} else {
				smallGramWord = bigGramTokens[0];
			}
			int smallCount = smallGram.get(smallGramWord);
			printOneLine(bigCount, smallCount, curWord);
		}
		*/
		Object[] result = Helper.toOrder(bigGram);
		Object[] freqs = (Object[])result[0];
		@SuppressWarnings("unchecked")
		Map<Integer, List<String>> freqToWords = (Map<Integer, List<String>>)result[1];
		for(int i = freqs.length-1; i >= 0; i--) {
			List<String> curWords = freqToWords.get((Integer)freqs[i]);
			for(String curWord : curWords) {
				String smallGramWord = null;
				String[] bigGramTokens = curWord.split(" ");
				if(bigGramTokens.length == 3) {
					smallGramWord = bigGramTokens[0]+" "+bigGramTokens[1];
				} else {
					smallGramWord = bigGramTokens[0];
				}
				int smallCount = smallGram.get(smallGramWord);
				printOneLine((Integer)freqs[i], smallCount, curWord);
			}
		}
	}
	
	private static void buildCount(String file, Map<String, Integer> unigramCount, Map<String, Integer> bigramCount, Map<String, Integer> trigramCount) throws FileNotFoundException {
		Scanner input = new Scanner(new File(file));  //may have problems here
		while(input.hasNextLine()) {
			String curLine = input.nextLine();
			String[] elements = curLine.split(" ");
			if(elements.length == 2) {  //unigram
				unigramCount.put(elements[1], Integer.parseInt(elements[0]));  //? is the order correct?
			} else if(elements.length == 3) {
				bigramCount.put(elements[1]+" "+elements[2], Integer.parseInt(elements[0]));
			} else {
				trigramCount.put(elements[1]+" "+elements[2]+" "+elements[3], Integer.parseInt(elements[0]));
			}
		}
		input.close();
	}
	
	public static String d(Object s) {
		System.out.println("debug: "+s.toString());
		return s.toString();
	}
}
