import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;

//note: you must run this file with c.java
public class AnalyzeFeatures {
	
	public static void main(String[] args) throws IOException {
		String[] leftDir = {"/home2/yz48/dropbox/13-14/570/hw10/examples/training/left"};
		String[] rightDir = {"/home2/yz48/dropbox/13-14/570/hw10/examples/training/right"};
		//<abs minus>
		SortedMap<Double, List<String>> abs = absoluteMinus(leftDir, rightDir);
		Stack<String> reverse = new Stack<String>();
		for(double cur : abs.keySet()) {
			List<String> curList = abs.get(cur);
			for(String curStr : curList) {
				reverse.push(curStr+" "+cur);
			}
		}
		PrintStream outputFile = new PrintStream("absminus_out");
		while(!reverse.isEmpty()) {
			outputFile.println(reverse.pop());
		}
		outputFile.close();
		//</abs minus>
		
		//<exclusive>
		List<Set<String>> exclu = exclusiveWords(leftDir, rightDir);
		Queue<String> outque = new PriorityQueue<String>();
		for(String cur : exclu.get(0)) {
			outque.add(cur);
		}
		for(String cur : exclu.get(1)) {
			outque.add(cur);
		}
		outputFile = new PrintStream("exclusive_out");
		while(!outque.isEmpty()) {
			outputFile.println(outque.remove());
		}
		outputFile.close();
		//</exclusive>
	}
	
	//pre: leftDirs: dirs contains left blogs; leftDirs: dirs contains right blogs
	//post: the words are sorted by it's abs probability. use iterator to access the key set, then you can get words in assending order 
	public static SortedMap<Double, List<String>> absoluteMinus(String[] leftDirs, String[] rightDirs) throws IOException {
		c.outBuffer = new StringBuilder();
		Map<String, Integer> leftCount = countWords(leftDirs);
		Map<String, Integer> rightCount = countWords(rightDirs);
		//int leftSum = sumCount(leftCount);
		//int rightSum = sumCount(rightCount);
		Stack<String> leftProb = c.getWordDecentProbOrder(leftCount);
		Stack<String> rightProb = c.getWordDecentProbOrder(rightCount);
		Map<String, Double> leftProbMap = new HashMap<String, Double>();
		Map<String, Double> rightProbMap = new HashMap<String, Double>();
		
		
		while(!leftProb.isEmpty()) {
			String[] info = leftProb.pop().split(" ");
			leftProbMap.put(info[0], Double.parseDouble(info[1]));
		}
		
		while(!rightProb.isEmpty()) {
			String[] info = rightProb.pop().split(" ");
			rightProbMap.put(info[0], Double.parseDouble(info[1]));
		}
		
		
		Map<String, Double> totalProbMap = new HashMap<String, Double>();
		for(String cur : leftProbMap.keySet()) {
			if(rightProbMap.containsKey(cur)) {
				
				totalProbMap.put(cur, Math.abs(leftProbMap.get(cur)-rightProbMap.get(cur))/(leftProbMap.get(cur)+rightProbMap.get(cur)));
			}
		}
		SortedMap<Double, List<String>> ret = c.produceFreqToToken(totalProbMap);
		return ret;
	}
	
	//pre: leftDirs: dirs contains left blogs; leftDirs: dirs contains right blogs
	//post: first element is left exclusive word set, second is right word set
	public static List<Set<String>> exclusiveWords(String[] leftDirs, String[] rightDirs) throws IOException {
		List<Set<String>> ret = new ArrayList<Set<String>>();
		
		Map<String, Integer> leftCount = countWords(leftDirs);
		Map<String, Integer> rightCount = countWords(rightDirs);
		Set<String> leftESet = new HashSet<String>();
		for(String cur : leftCount.keySet()) {
			if(!rightCount.containsKey(cur)) {
				leftESet.add(cur);
			}
		}
		ret.add(leftESet);
		Set<String> rightESet = new HashSet<String>();
		for(String cur : rightCount.keySet()) {
			if(!leftCount.containsKey(cur)) {
				rightESet.add(cur);
			}
		}
		ret.add(rightESet);
		return ret;
	}

	
	private static Map<String, Integer> countWords(String[] dirs) throws IOException {
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for(int i = 0; i < dirs.length; i++) {
			PriorityQueue<String> allFileCurDir = c.getAllFiles(dirs[i]);
			while(!allFileCurDir.isEmpty()) {
				BufferedReader br =  c.readFileByLine(dirs[i]+"/"+allFileCurDir.remove());
				String line = null;
				while((line = br.readLine()) != null) {
					String[] toks = TokProbCount.getToks(line);
					for(int j = 0; j < toks.length; j++) {
						if(!toks[j].equals("")) {
							c.increaseCount(countMap, toks[j]);  // if null, exception
						}
					}
				}
				br.close();
			}
		}
		return countMap;
	}

}
