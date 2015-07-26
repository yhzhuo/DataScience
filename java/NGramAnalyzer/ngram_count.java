import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;


public class ngram_count {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			throw new Exception("The length of arguments is not correct");
		}
		String trainingData = args[0];
		String content = readFile(trainingData);
		Map<String, Integer> unigram = new HashMap<String, Integer>();
		Map<String, Integer> bigram = new HashMap<String, Integer>();
		Map<String, Integer> trigram = new HashMap<String, Integer>();
		Scanner s = new Scanner(content);
		while(s.hasNextLine()) {
			String sentence = s.nextLine();
			processEachSentence(unigram, bigram, trigram, sentence);
		}
		s.close();
		Map<Integer, List<String>> unigramFreq = produceFreqToToken(unigram);
		Map<Integer, List<String>> bigramFreq = produceFreqToToken(bigram);
		Map<Integer, List<String>> trigramFreq = produceFreqToToken(trigram);
		
		printFrequency(unigramFreq);
		printFrequency(bigramFreq);
		printFrequency(trigramFreq);
	}
	
	private static SortedMap<Integer, List<String>> produceFreqToToken(Map<String, Integer> gramMap) {
		SortedMap<Integer, List<String>> ret = new TreeMap<Integer, List<String>>();
		for(String token : gramMap.keySet()) {
			int freq = gramMap.get(token);
			if(ret.containsKey(freq)) {
				ret.get(freq).add(token);
			} else {
				List<String> tokenList = new LinkedList<String>();
				tokenList.add(token);
				ret.put(freq, tokenList);
			}
		}
		return ret;
	}
	
	private static void printFrequency(Map<Integer, List<String>> gramSortFreq) {
		List<Integer> freqList = new LinkedList<Integer>();
		for(int freq : gramSortFreq.keySet()) {
			freqList.add(freq);
		}
		Object[] occurArray = freqList.toArray();
		for(int i = occurArray.length - 1; i >= 0; i--) {
			int curFreq = (Integer)occurArray[i];
			List<String> tokens = gramSortFreq.get(curFreq);
			for(String cur : tokens) {
				System.out.println(curFreq+" "+cur);
			}
		}
	}
	
	private static void processEachSentence(Map<String, Integer> unigram, Map<String, Integer> bigram, Map<String, Integer> trigram, String sentence) {
		String[] tokens = sentence.split(" ");
		for(int i = 0; i < tokens.length; i++) {
			//unigram
			addGram(tokens[i], unigram);
			//bigram
			if(i < tokens.length-1) {
				addGram(tokens[i]+" "+tokens[i+1], bigram);
			}
			//trigram
			if(i < tokens.length-2) {
				addGram(tokens[i]+" "+tokens[i+1]+" "+tokens[i+2], trigram);
			}
		}
	}
	
	private static void addGram(String gram, Map<String, Integer> gramMap) {
		if(gramMap.containsKey(gram)) {
			gramMap.put(gram, gramMap.get(gram)+1);
		} else {
			gramMap.put(gram, 1);
		}
	}
	
	private static String readFile(String file) throws IOException {
		StringBuilder ret = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			ret.append("<s> "+line+" </s>\n");
		}
		reader.close();
		return ret.toString();
	}
	
	public static String debug(Object s) {
		System.out.println(s.toString());
		return s.toString();
	}
}
