import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * @author Yaohua
 * This is only the MarKovChain for String
 */
public class MarkovChain {
	
	private Map<String, Graph<String, Long>> tranMaxs;
	private boolean trained;
	private int gramNum;
	private long autoIncrement;
	private static final double ZERO_MARCO = Math.log(0.0000000000001);
	
	public MarkovChain(int gramNum) {
		this.gramNum = gramNum;
		initialize();
	}
	
	public void initialize() {
		tranMaxs = new HashMap<String, Graph<String, Long>>();
		//allCate = new HashSet<String>();
		trained = false;
		autoIncrement = 0;
	}
	
	/**
	 * 
	 * @param featVector: the feature vectors of training set, should only have two elements
	 * The input should strictly merely have words need to be trained. Other characters will be deleted
	 */
	public void train(List<String[]> featVectors) {
		preprocess(featVectors);
		for (String[] cur : featVectors) {
			Graph<String, Long> curM = tranMaxs.get(cur[0]);
			for (int i = 1; i < cur[1].length(); i++) {
				String curState = cur[1].charAt(i)+"";
				String dependOn = cur[1].substring(Math.max(0, i-gramNum), i);  //be careful about the math here
				curM.addNode(curState); curM.addNode(dependOn);     //dependOn transit to curState
				curM.addEdge(dependOn, curState, autoIncrement);
				autoIncrement++;
			}
		}
		trained = true;
	}
	
	
	public void test(List<String[]> featVectors) {
		if (!trained) {
			throw new RuntimeException("You must train MarkovChain first");
		}
		preprocess(featVectors);
		int totalCount = 0;
		int correctCount = 0;
		for (String[] cur : featVectors) {
			Stack<String> res = predictHelper(cur[1]);
			String[] prediction = res.peek().split(" ");
			c.o("<info>");
			c.o("Current Name: "+cur[1]+" Current Race: "+cur[0]+" Predicted Race: "+prediction[0]);
			totalCount++;  //TODO: would this iterator with ++ result in problems?
			if (cur[0].equals(prediction[0])) {
				correctCount++;
				c.o("prediction is correct!");
			} else {
				c.d("prediction is wrong!");
			}
			
			c.o("</info>");
		}
		c.o("accuracy: "+((double) correctCount/totalCount));
	}
	
	private Stack<String> predictHelper(String featVector) {
		Map<String, Double> probs = new HashMap<String, Double>();
		for (String s : tranMaxs.keySet()) {   // TODO: This step here is not being optimized
			probs.put(s, 0.0);
		}
		for (int i = 1; i < featVector.length(); i++) {
			String curState = featVector.charAt(i)+"";
			String dependOn = featVector.substring(Math.max(0, i-gramNum), i);
			for (Map.Entry<String, Double> e : probs.entrySet()) {
				Graph<String, Long> curM = tranMaxs.get(e.getKey());
				int totalTimes = 0;
				int curTimes = 0;
				c.o(dependOn);
				Map<String, Set<Long>> allPossibleNext = curM.getAllChildren(dependOn);
				/*
				if (allPossibleNext == null) {
					c.o("allPossibleNext is null");
				} else {
					c.o("allPossibleNext is not null");
				}
				*/
				
				if (allPossibleNext != null) {
					Iterator<Map.Entry<String, Set<Long>>> itr = allPossibleNext.entrySet().iterator();
					while (itr.hasNext()) {
						Map.Entry<String, Set<Long>> curPair = itr.next();
						if (curPair.getKey().equals(curState)) {
							curTimes += curPair.getValue().size();
						}
						totalTimes += curPair.getValue().size();
					}
				}
				
				
				
				
				double toAdd = curTimes == 0 ? ZERO_MARCO : Math.log((double) curTimes/totalTimes);
				e.setValue(e.getValue()+toAdd);
			}
		}
		
		//<calculate real probability>
		for (Map.Entry<String, Double> e : probs.entrySet()) {
			e.setValue(Math.pow(Math.E, e.getValue()));
		}
		//</calculate real probability>
		return c.getWordDecentOrder(probs);   //TODO: the probability is still not being normalized
	}
	
	/**
	 * 
	 * @param featVectors: the input feature vectors, it will be preprocessed inside.
	 * @return the list of prediction results represented by decreasing scores
	 */
	public List<Stack<String>> predict(List<String> featVectors) {
		List<Stack<String>> ret = new LinkedList<Stack<String>>();
		for (String s : featVectors) {
			ret.add(predictHelper(preprocessName(s)));
		}
		return ret;
	}
	
	public static String preprocessName(String s) {
		s = "$"+s.trim().toLowerCase().replaceAll("[^a-z0-9 ]", "")+".";
		if (s.length() == 2) {
			throw new IllegalArgumentException();
		}
		return s;
	}
	
	public static String preprocessTag(String s) {
		s = s.trim().replaceAll("[^a-zA-Z0-9 ]", "");
		if (s.length() == 0) {
			throw new IllegalArgumentException();
		}
		return s;
	}
	
	/**
	 * 
	 * @param featVector: feature vectors of training set, should only have two elements
	 * The input should strictly merely have words need to be trained. Other characters will be deleted
	 * 
	 * @throws IllegalArgumentException if feature vectors contains null or length != 2
	 */
	private void preprocess(List<String[]> featVector) {
		if (featVector == null) {
			throw new IllegalArgumentException();
		}
		ListIterator<String[]> itr = (ListIterator<String[]>) featVector.listIterator();
		while (itr.hasNext()) {
			String[] cur = itr.next();
			if (cur == null || cur.length != 2) {
				throw new IllegalArgumentException();
			}
			cur[0] = preprocessTag(cur[0]);
			cur[1] = preprocessName(cur[1]);
			if (!tranMaxs.containsKey(cur[0])) {
				tranMaxs.put(cur[0], new Graph<String, Long>());
			}
		}
	}
}
