/**
 * Author: ZHUO, Yaohua
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


public class MakeFeatureVectors {

	private static void buildFeat2AbsMap() {
		feat2AbsMap = new HashMap<String, String>();
		//TODO: haotian should set the bound
		feat2AbsMap.put("F5", "absMinus_0.4_0.5");   ////convension: absMinus_x_y=1 means lower bound = x upper bound = y
		feat2AbsMap.put("F6", "absMinus_0.3_0.45");
		feat2AbsMap.put("F7", "absMinus_0.7_0.8");
	}
	
	
	
	
	
	private static String task;
	//private static List<String> params;
	
	//private static paramFile
	
	private static final String[] leftDirs = {"/home2/yz48/dropbox/13-14/570/hw10/examples/training/left"};
	private static final String[] rightDirs = {"/home2/yz48/dropbox/13-14/570/hw10/examples/training/right"};
	
	private static Set<String> exclusive = null;
	private static Set<String> absMinus = null;
	
	private static Map<String, String> feat2AbsMap = null;
	
	public static Map<String, Integer> leftCount = null;
	public static Map<String, Integer> rightCount = null;
	
	
	
	
	public static void main(String[] args) throws IOException {
		//<get inputs>
		
		//</get inputs>
		
		
		
		
		
		
		
		task = args[0];
		if(task.equals("proc_file1")) {
			Set<String> parameters = new HashSet<String>();
			parameters.add("F1");  // F1 is unigram feature
			
			PrintStream outputFile = new PrintStream(args[3]);
			outputFile.println(processOneFile(parameters, args[2], args[1]));
			outputFile.close();
			//args[3] the output file
		} else if(task.equals("create_vectors1")) {
			Set<String> parameters = new HashSet<String>();
			String[] dirs = new String[args.length-2];
			for(int i = 2; i < args.length; i++) {
				dirs[i-2] = args[i];
			}
			parameters.add("F1");
			outputVecFiles(dirs, parameters, args[1]);
		} else if(task.equals("proc_file2")) {
			String paramFile = args[1];
			Set<String> parameters = getParams(paramFile);
			PrintStream outputFile = new PrintStream(args[4]);
			outputFile.println(processOneFile(parameters, args[3], args[2]));
			outputFile.close();
			
			//args[4] the output file
		} else if(task.equals("create_vectors2")) {
			String paramFile = args[1];
			Set<String> parameters = getParams(paramFile);
			String[] dirs = new String[args.length-3];
			for(int i = 3; i < args.length; i++) {
				dirs[i-3] = args[i];
			}
			outputVecFiles(dirs, parameters, args[2]);
		} else {
			throw new IllegalArgumentException("The first parameter is wrong");
		}
	}
	
	//pre: the elements in dirs should not end with /
	private static void outputVecFiles(String[] dirs, Set<String> parameters, String outputPath) throws IOException {
		//StringBuilder out = new StringBuilder();
		PrintStream outputFile = new PrintStream(outputPath);
		for(String dir : dirs) {
			PriorityQueue<String> allFileCurDir = getAllFiles(dir);
			String[] labelInfo = dir.split("/");
			String label = labelInfo[labelInfo.length-1];
			while(!allFileCurDir.isEmpty()) {
				outputFile.println(processOneFile(parameters, label, dir+"/"+allFileCurDir.remove()));
			}
		}
		outputFile.close();
	}
	
	
	//post: get parameters from parameter file, and return it
	//if absMinus and exclusive exist, make them
	private static Set<String> getParams(String paramFile) throws IOException {
		BufferedReader br = c.readFileByLine(paramFile);
		Set<String> ret = new HashSet<String>();
		String line;
		boolean isExclusive = false;
		List<double[]> thres = new LinkedList<double[]>();
		while((line = br.readLine()) != null) {
			if(!line.trim().equals("")) {
				String[] featGroup = line.split("=");
				if(Integer.parseInt(featGroup[1]) == 1) {
					if(featGroup[0].equals("F4") && !isExclusive) {   //F4 is the exclusive words
						isExclusive = true;
					} else if(Integer.parseInt(featGroup[0].replaceAll("[^0-9]", "")) > 4) {  //feat2AbsMap
						if(feat2AbsMap == null) {
							buildFeat2AbsMap();
						}
						//note: throw null pointer exception if featGroup[0] is not built in the feat2AbsMap
						String[] thresInfo = feat2AbsMap.get(featGroup[0]).split("_");  
						double[] thresArr = {Double.parseDouble(thresInfo[1]), Double.parseDouble(thresInfo[2])};  // may have problems here
						thres.add(thresArr);
					}
					ret.add(featGroup[0]);
				}
			}
		}
		if(isExclusive) {
			exclusiveWords(leftDirs, rightDirs);
		}
		if(!thres.isEmpty()) {
			makeAbsMinus(leftDirs, rightDirs, thres);
		}
		return ret;
	}
	
	
	
	//pre: input left dir, right dir and the threshold list: thres, each sub list of thres represent threshold.
	// first element of sub list of thres is the lower bound, second is the upper bount
	//post: make the Set: absMinus contains the features which within thresholds
	public static void makeAbsMinus(String[] leftDirs, String[] rightDirs, List<double[]> thres) throws IOException {
		makeLRCountMap(leftDirs, rightDirs);
		//<count total numbers>
		int leftTotalNum = countTotal(leftCount);
		int rightTotalNum = countTotal(rightCount);
		
		//</count total numbers>
		absMinus = new HashSet<String>();
		for(String cur : leftCount.keySet()) {
			if(rightCount.containsKey(cur)) {
				int curLeftCount = leftCount.get(cur);
				int curRightCount = rightCount.get(cur);
				//!alert: this make cause NaN
				double absMinus_v = Math.abs((double) curLeftCount/leftTotalNum - (double) curRightCount/rightTotalNum)/((double) curLeftCount/leftTotalNum + (double) curRightCount/rightTotalNum); // this step is important!
				for(double[] curThre : thres) {
					if(absMinus_v > curThre[0] && absMinus_v < curThre[1]) {
						absMinus.add(cur);
						break;
					}
				}
			}
		}
	}
	
	private static int countTotal(Map<String, Integer> map) {
		int ret = 0;
		Iterator<String> i = map.keySet().iterator();
		while(i.hasNext()) {
			ret += map.get(i.next());
		}
		return ret;
	}
	
	//pre: the first argument should not be args[0]
	//post: behaviour of this only depends on parameters
	//?: does each feature vector need a name like hw8?
	private static String processOneFile(Set<String> parameters, String label, String path) throws IOException {
		StringBuilder ret = new StringBuilder();
		BufferedReader br = c.readFileByLine(path);
		String line = null;
		ret.append(path+" ");
		ret.append(label+" ");
		//<build maps that contains different groups of features>
		/*
		Map<String, Integer> uniGram = new HashMap<String, Integer>();
		Map<String, Integer> biGram = new HashMap<String, Integer>();
		Map<String, Integer> triGram = new HashMap<String, Integer>();
		Map<String, Integer> exGram = new HashMap<String, Integer>();
		Map<String, Integer> absGram = new HashMap<String, Integer>();
		*/
		Map<String, Map<String, Integer>> gramMap = new HashMap<String, Map<String, Integer>>();
		gramMap.put("F1", new HashMap<String, Integer>());
		gramMap.put("F2", new HashMap<String, Integer>());
		gramMap.put("F3", new HashMap<String, Integer>());
		gramMap.put("F4", new HashMap<String, Integer>());
		gramMap.put("F5+", new HashMap<String, Integer>());
		
		//</build maps that contains different groups of features>
		while((line = br.readLine()) != null) {
			String[] toks = getToks(line);
			for(int i = 0; i < toks.length; i++) {
				if(parameters.contains("F1")) { //F1 is the icon of unigram
					c.increaseCount(gramMap.get("F1"), toks[i]);
				}
				if(parameters.contains("F2")) { // F2 is the bigram
					if(i < toks.length - 1) {
						c.increaseCount(gramMap.get("F2"), toks[i]+"_"+toks[i+1]);
					}
				}
				if(parameters.contains("F3")) { //try gram
					if(i < toks.length - 2) {
						c.increaseCount(gramMap.get("F3"), toks[i]+"_"+toks[i+1]+"_"+toks[i+2]);
					}
				}
				if(exclusive != null && exclusive.contains(toks[i])) { //exclusive features
					c.increaseCount(gramMap.get("F4"), toks[i]);
				}
				if(absMinus != null && absMinus.contains(toks[i])) {   //abs minus exclusive features
					if(i < toks.length - 2) {
						c.increaseCount(gramMap.get("F5+"), toks[i]+"_"+toks[i+1]+"_"+toks[i+2]);
					}
						
					if(i > 0 && i < toks.length - 1) {
						c.increaseCount(gramMap.get("F5+"), toks[i-1]+"_"+toks[i]+"_"+toks[i+1]);
					}
					
					if(i > 1) {
						c.increaseCount(gramMap.get("F5+"), toks[i-2]+"_"+toks[i-1]+"_"+toks[i]);
					}
					
				}
			}
			
		}
		boolean isAddFeatGroup = task.endsWith("2");
		Queue<String> outque = new PriorityQueue<String>();
		for(String key : gramMap.keySet()) {
			Map<String, Integer> curMap = gramMap.get(key);
			for(String curTok : curMap.keySet()) {
				if(!isAddFeatGroup) {
					outque.add(curTok+" "+curMap.get(curTok)+" ");
				} else {
					outque.add(key+"="+curTok+" "+curMap.get(curTok)+" ");
				}
			}
		}
		while(!outque.isEmpty()) {
			ret.append(outque.remove());
		}
		return ret.toString().trim();
	}
	
	
	/*
	private static void makeExclusiveFeat() {
		
	}
	*/
	
	private static void makeLRCountMap(String[] leftDirs, String[] rightDirs) throws IOException {
		if(leftCount == null) {
			leftCount = countWords(leftDirs);
		}
		if(rightCount == null) {
			rightCount = countWords(rightDirs);
		}
	}
	
	public static void exclusiveWords(String[] leftDirs, String[] rightDirs) throws IOException {
		//List<Set<String>> ret = new ArrayList<Set<String>>();
		//Map<String, Integer> leftCount = countWords(leftDirs);
		//Map<String, Integer> rightCount = countWords(rightDirs);
		makeLRCountMap(leftDirs, rightDirs);
		exclusive = new HashSet<String>();
		for(String cur : leftCount.keySet()) {
			if(!rightCount.containsKey(cur)) {
				exclusive.add(cur);
			}
		}
		for(String cur : rightCount.keySet()) {
			if(!leftCount.containsKey(cur)) {
				exclusive.add(cur);
			}
		}
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
	
	
	
	private static PriorityQueue<String> getAllFiles(String dirPath) {
		PriorityQueue<String> allFiles = new PriorityQueue<String>();
		String file;
		File folder = new File(dirPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				file = listOfFiles[i].getName();
				allFiles.add(file);
			}
		}
		return allFiles;
	}
	
	public static String[] getToks(String line) {
		//may have problems: notice, this may contains empty string, check the out put file
		String res = line.replaceAll("[^0-9\\-a-zA-Z]", " ").toLowerCase();
		String[] temp = res.split("\\s+");
		List<String> ret = new ArrayList<String>();
		for(int i = 0; i < temp.length; i++) {
			if(!temp[i].equals("")) {
				ret.add(temp[i]);
			}
		}
		String[] retArr = new String[ret.size()];
		for(int i = 0; i < ret.size(); i++) {
			retArr[i] = ret.get(i);
		}
		return retArr;    // may have problems, the order may be wrong.
		//String[] ret = 
	}
}
