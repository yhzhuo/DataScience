import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import java.util.SortedMap;
import java.util.TreeMap;


public class build_dt {
	
	private static String trainPath;
	private static String testPath;
	private static int maxDepth;
	private static double minGain;
	private static String modelPath;
	private static String sysOutput;
	
	public static double POS_INF = Double.MAX_VALUE;
	public static double NEG_INF = -Double.MAX_VALUE;
	
	private static Map<String, Set<Integer>> features;  // features of instances. Set<Integer> contains features which has the instance
	private static int id = 1;                            // auto increment instances id
	private static Map<String, Set<Integer>> categories;       // map categories to instances' id
	
	public static void main(String[] args) throws IOException {
		
		//<initialization>
		//<get arguments>
		trainPath = args[0];
		testPath = args[1];
		maxDepth = Integer.parseInt(args[2]);
		minGain = Double.parseDouble(args[3]);
		modelPath = args[4];
		sysOutput = args[5];
		//</get arguments>
		
		//</initialization>
		readInstances();
		DecisionTree dt = ID3();
		dt.output(modelPath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(sysOutput, true));
		Map<String, Map<String, Integer>> trainMatrix = classification(dt, trainPath, bw);
		Map<String, Map<String, Integer>> testMatrix = classification(dt, testPath, bw);
		bw.close();
		outputMatrix(trainMatrix, "training");
		outputMatrix(testMatrix, "test");
	}
	
	/**
	 * 
	 * @param matrix: realCate, predictCate, number
	 * @param name
	 * @throws IOException
	 */
	private static void outputMatrix(Map<String, Map<String, Integer>> matrix, String name) throws IOException {
		c.o("Confusion matrix for the "+name+" data:");
		c.o("row is the truth, column is the system output");
		c.o("");
		StringBuilder title = new StringBuilder();
		Map<String, StringBuilder> realCates = new TreeMap<String, StringBuilder>();
		//title.append("             ");
		for(String cate : matrix.keySet()) {
			title.append(cate+" ");
			realCates.put(cate, new StringBuilder());
		}
		int totalIns = 0;
		int rightIns = 0;
		for(String cate : realCates.keySet()) {
			Map<String, Integer> resCate = matrix.get(cate);
			StringBuilder curLine = realCates.get(cate);
			for(String predict : resCate.keySet()) {
				int curIns = resCate.get(predict);
				curLine.append(curIns+" ");
				totalIns += curIns;
				if(cate.equals(predict)) {
					rightIns += curIns;
				}
			}
		}
		c.o("             "+title.toString().trim());
		for(String cate : realCates.keySet()) {
			StringBuilder curLine = new StringBuilder();
			curLine.append(cate);
			curLine.append(realCates.get(cate).toString().trim());
			c.o(curLine.toString());
		}
		c.o("");
		c.o(" "+name.substring(0,1).toUpperCase()+name.substring(1, name.length())+" accuracy="+((double)rightIns/totalIns));
		c.o("");
	}
	
	private static Map<String, Map<String, Integer>> classification(DecisionTree dt, String inputPath, BufferedWriter bw) throws IOException {
		//realCate, predictCate, number
		Map<String, Map<String, Integer>> ret = new TreeMap<String, Map<String, Integer>>();
		//PrintStream ps = new PrintStream(filePath);
		BufferedReader r = c.readFileByLine(inputPath);
		String line;
		int lineNum = 1;
		Set<String> feats = new HashSet<String>();
		while((line = r.readLine()) != null) {
			//<get features and true label>
			String[] info = line.trim().split(" ");
			for(int i = 1; i < info.length; i++) {
				feats.add(info[i].split(":")[0]);
			}
			//</get features and true label>
			Map<String, Double> res = dt.classify(feats);
			//<print classification result>
			Queue<String> order = new PriorityQueue<String>();
			for(String cate : res.keySet()) {
				order.add(cate+"\t"+res.get(cate));
			}
			StringBuilder resOut = new StringBuilder();
			resOut.append("line:"+lineNum+" ");
			while(!order.isEmpty()) {
				resOut.append(order.remove());
			}
			//ps.println(resOut.toString().trim());
			bw.write(resOut.toString().trim());
			bw.write("\n");
			//</print classification result>
			
			//<get confusion matrix>
			String resCate = null;
			double maxProb = -1.0;
			Iterator<String> i = res.keySet().iterator();
			while(i.hasNext()) {
				String curResCate = i.next();
				double curResProb = res.get(curResCate);
				if(curResProb > maxProb) {
					maxProb = curResProb;
					resCate = curResCate;
				}
			}
			if(!ret.containsKey(info[0])) {
				ret.put(info[0], new TreeMap<String, Integer>());
			}
			c.increaseCount(ret.get(info[0]), resCate);
			//</get confusion matrix>
			feats.clear();
			lineNum++;
		}
		//ps.close();
		return ret;
	}
	
	private static void readInstances() throws IOException {
		features = new HashMap<String, Set<Integer>>();
		categories = new HashMap<String, Set<Integer>>();
		BufferedReader r = c.readFileByLine(trainPath);
		String line;
		while((line = r.readLine()) != null) {
			int newId = getNewId();
			String info[] = line.split(" ");
			for(int i = 0; i < info.length; i++) {
				if(i == 0) {
					readInstancesHelper(categories, info[i], newId);
				} else {
					String featName = info[i].split(":")[0];
					readInstancesHelper(features, featName, newId);
				}
			}
		}
	}
	
	private static void readInstancesHelper(Map<String, Set<Integer>> m, String s, int newId) {
		if(m.containsKey(s)) {
			m.get(s).add(newId);
		} else {
			Set<Integer> newInsSet = new HashSet<Integer>();
			newInsSet.add(newId);
			m.put(s, newInsSet);
		}
	}
	
	private static int getNewId() {
		id++;
		return id-1;
	}
	
	/**
	 * 
	 * @param instances: map from instance id to the categories it belongs to
	 * @return
	 */
	private static double getEntropy(Map<Integer, String> instances) {
		Map<String, Integer> cate = new HashMap<String, Integer>();
		for(int cur : instances.keySet()) {
			String curCate = instances.get(cur);
			c.increaseCount(cate, curCate);
		}
		return getEntropyHelper(cate);
	}
	
	
	/**
	 * 
	 * @param cate: map categories to it's instance number
	 * @return: the calculated entropy
	 */
	private static double getEntropyHelper(Map<String, Integer> cate) {
		int sum = 0;
		for(String cur : cate.keySet()) {
			sum += cate.get(cur);
		}
		double ret = 0.0;
		for(String cur : cate.keySet()) {
			int curCount = cate.get(cur);
			if(curCount == 0) {
				/*
				 * when p gets close to zero (i.e., the category has only a few examples in it), then the log(p)
				 *  becomes a big negative number, but the p part dominates the calculation, so the entropy works out to be nearly zero.
				 *  so we don't do anything.
				 */
				continue; 
			}
			double prob = (double) curCount/sum;
			ret += -prob*c.log(prob, 2);
		}
		return ret;
	}
	
	
	/*
	private static double getInfoGain(Map<Integer, String> setS, List<Map<Integer, String>> setSv) {
		double entropyS = getEntropy(setS);
		double svSum = 0.0;
		for(Map<Integer, String> cur : setSv) {
			svSum += (double) cur.keySet().size()/setS.keySet().size()*getEntropy(cur);
		}
		return entropyS-svSum;
	}
	*/
	
	private static double getInfoGain(double entropyS, int sizeS, List<Map<Integer, String>> setSv) {
		//double entropyS = getEntropy(setS);
		double svSum = 0.0;
		for(Map<Integer, String> cur : setSv) {
			svSum += (double) cur.keySet().size()/sizeS*getEntropy(cur);
		}
		return entropyS-svSum;
	}
	
	
	private static DecisionTree ID3() {
		DtNode root = new DtNode();
		Map<Integer, String> allInstanceIds = new HashMap<Integer, String>();
		for(String cur : categories.keySet()) {
			Set<Integer> curSet = categories.get(cur);
			for(int curId : curSet) {
				allInstanceIds.put(curId, cur);
			}
		}
		ID3Helper(root, allInstanceIds);
		return new DecisionTree(root);
	}
	
	private static void ID3Helper(DtNode curNode, Map<Integer, String> curIns) {
		Map<String, String> usedFeatures = traceBack(curNode);
		//<if current depth >= maxDepth, finish>
		if(usedFeatures.size()+1 >= maxDepth) {
			finishBranch(curNode, curIns);
			return;
		}
		//</if current depth >= maxDepth, finish>
		//<get info gain and choose feature>
		double entropyS = getEntropy(curIns);
		double maxInfoGain = Double.NEGATIVE_INFINITY;
		String feature2Choose = null;
		for(String curFeat : features.keySet()) {
			if(!usedFeatures.containsKey(curFeat)) {
				
				Map<Integer, String> hasFeatMap = new HashMap<Integer, String>();
				Map<Integer, String> noFeatMap = new HashMap<Integer, String>();
				List<Map<Integer, String>> setSv = new LinkedList<Map<Integer, String>>();
				setSv.add(hasFeatMap);
				setSv.add(noFeatMap);
				for(int curId : curIns.keySet()) {
					if(features.get(curFeat).contains(curId)) {
						hasFeatMap.put(curId, curIns.get(curId));
					} else {
						noFeatMap.put(curId, curIns.get(curId));
					}
				}
				double curInfoGain = getInfoGain(entropyS, curIns.keySet().size(), setSv);
				if(feature2Choose == null || curInfoGain > maxInfoGain) {
					feature2Choose = curFeat;
					maxInfoGain = curInfoGain;
				}
			}
		}
		//</get info gain and choose feature>
		if(feature2Choose == null || maxInfoGain < minGain) {
			finishBranch(curNode, curIns);
			return;
		}
		//<draw branches>
		DtNode n0 = new DtNode(feature2Choose, curNode, "0");
		DtNode n1 = new DtNode(feature2Choose, curNode, "1");
		curNode.children.put("0", n0);
		curNode.children.put("1", n1);
		//</draw branches>
		
		//<create sub Sv and analyze them>
		Map<Integer, String> sv0 = new HashMap<Integer, String>();
		Set<String> sv0Cate = new HashSet<String>();
		Map<Integer, String> sv1 = new HashMap<Integer, String>();
		Set<String> sv1Cate = new HashSet<String>();
		Iterator<Map.Entry<Integer, String>> i = curIns.entrySet().iterator();
		Set<Integer> insWithFeat = features.get(feature2Choose);
		
		Map<String, Integer> curCateCount = new HashMap<String, Integer>();
		while(i.hasNext()) {
			Map.Entry<Integer, String> pairs = i.next();
			if(insWithFeat.contains(pairs.getKey())) {
				sv1.put(pairs.getKey(), pairs.getValue());
				sv1Cate.add(pairs.getValue());
			} else {
				sv0.put(pairs.getKey(), pairs.getValue());
				sv0Cate.add(pairs.getValue());
			}
			c.increaseCount(curCateCount, pairs.getValue());
			i.remove(); //?: will this really increase speed?
		}
		int maxCount = -1;
		String curInsFirstCate = null;
		//Iterator<String> j = curCateCount
		Iterator<Map.Entry<String, Integer>> j = curCateCount.entrySet().iterator();
		while(i.hasNext()) {
			Map.Entry<String, Integer> pairs = j.next();
			if(pairs.getValue() > maxCount) {
				maxCount = pairs.getValue();
				curInsFirstCate = pairs.getKey();
			}
		}
		ID3step3Helper(curInsFirstCate, n0, sv0, sv0Cate);
		ID3step3Helper(curInsFirstCate, n1, sv1, sv1Cate);
		//</create sub Sv and analyze them>
	}
	
	private static void ID3step3Helper(String parentFirstCate, DtNode curNode, Map<Integer, String> sv, Set<String> svCate) {
		if(sv.isEmpty()) {
			curNode.result = new HashMap<String, Double>();
			curNode.result.put(parentFirstCate, 1.0);
			curNode.instanceNum = 0;
		} else if(svCate.size() == 1) {
			curNode.result = new HashMap<String, Double>();
			for(String cur : svCate) {
				curNode.result.put(cur, 1.0);  //TODO: may need instance number
				break;
			}
			curNode.instanceNum = sv.size();
		} else {
			ID3Helper(curNode, sv);
		}
	}
	
	
	private static void finishBranch(DtNode curNode, Map<Integer, String> curIns) {
		Map<String, Integer> catesMap = new HashMap<String, Integer>();
		for(int id : curIns.keySet()) {
			c.increaseCount(catesMap, curIns.get(id));
		}
		curNode.result = new HashMap<String, Double>();
		for(String curCate : catesMap.keySet()) {
			curNode.result.put(curCate, (double)catesMap.get(curCate)/curIns.size()); //TODO: may need instance number
		}
		curNode.instanceNum = curIns.size();
	}
	
	private static Map<String, String> traceBack(DtNode n) {
		Map<String, String> ret = new HashMap<String, String>();
		while(n.parent != null) {
			ret.put(n.parent.featName,n.parentValue);
			n = n.parent;
		}
		return ret;
	}
	
	//TODO: be ware of the NaN and infinity cases.
}
