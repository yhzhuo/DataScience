import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class Helper {

	//pre: input a Map<String, Integer>, string is the word, and Integer is the count
	//post: return {countArr, Map<Integer, List<String>>}
	public static Object[] toOrder(Map<String, Integer> wordToCount) {
		SortedMap<Integer, List<String>> result = produceFreqToToken(wordToCount);
		List<Integer> freqList = new LinkedList<Integer>();
		for(int freq : result.keySet()) {
			freqList.add(freq);
		}
		Object[] occurArray = freqList.toArray();
		Object[] ret = {occurArray, result};
		return ret;
	}
	
	public static SortedMap<Integer, List<String>> produceFreqToToken(Map<String, Integer> gramMap) {
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
}
