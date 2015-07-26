import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;


public class rank_feat_by_chi_square {
	//				//class       featName   true false count 
	//private static Map<String, Map<String, double[]>> table;
	private static Map<String, Integer> cateCount;
	private static Map<String, Map<String, Integer>> cateFeatCount;
	//docFreq is the number of documents that the feature occurs in
	private static Map<String, Integer> docFreq;
	private static int vectorCount = 0;
	
	public static void main(String[] args) throws IOException {
		initialization();
		buildTable();
		output();
	}
	
	private static void buildTable() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while((line = br.readLine()) != null) {
			String[] info = line.trim().split(" ");
			
			if(!cateFeatCount.containsKey(info[0])) {
				cateFeatCount.put(info[0], new HashMap<String, Integer>());
				
			}
			c.increaseCount(cateCount, info[0]);
			//Set<String> docFreqAlreadyCount = new HashSet<String>();
			for(int i = 1; i < info.length; i++) {
				String word = info[i].split(":")[0];
				c.increaseCount(cateFeatCount.get(info[0]), word);
				c.increaseCount(docFreq, word);
				/*
				if(!docFreqAlreadyCount.contains(info[i])) {
					c.increaseCount(docFreq, info[i]);
				}
				*/
			}
			vectorCount++;
		}
		//c.d(docFreq.size());
		
	}
	
	private static void initialization() {
		//table = new HashMap<String, Map<String, double[]>>();
		cateCount = new HashMap<String, Integer>();
		cateFeatCount = new HashMap<String, Map<String, Integer>>();
		docFreq = new HashMap<String, Integer>();
	}
	
	private static void output() throws IOException {
		Map<String, Double> chi = getChiSquare();
		//SortedMap<Double, List<String>> res = c.produceFreqToToken(chi);
		Stack<String> res = c.getWordDecentOrder(chi);
		while(!res.isEmpty()) {
			String[] info = res.pop().split(" ");
			c.o(info[0]+" "+info[1]+" "+docFreq.get(info[0]));
		}
	}
	
	private static Map<String, Double> getChiSquare() {
		//Map<String, Double> ret = new HashMap<String, Double>();
		//double[] is a array with 2 elements, before change to expect, first element is row sum, second is column sum
 		//before change to chi, Double is the positive row sum, column sum is expressed by cateCount's mapping
		Map<String, Double> chi = new HashMap<String, Double>();
		//<positive row sum>
		for(String curCate : cateFeatCount.keySet()) {
			Map<String, Integer> featCount = cateFeatCount.get(curCate);
			for(String curFeat : featCount.keySet()) {
				if(chi.containsKey(curFeat)) {
					chi.put(curFeat, chi.get(curFeat)+featCount.get(curFeat));
				} else {
					chi.put(curFeat, (double)featCount.get(curFeat));
				}
			}
		}
		//</positive row sum>
		//<calcualte chi square>
		for(String curFeat : chi.keySet()) {
			double posRowSum = chi.get(curFeat);
			double chiSquare = 0.0;
			for(String curCate : cateFeatCount.keySet()) {
				int curCatePosFeatCount = 0;
				Integer temp = cateFeatCount.get(curCate).get(curFeat);
				if(temp != null) {
					curCatePosFeatCount = temp.intValue();
				}
				int curCateCount = cateCount.get(curCate);
				double posExp = posRowSum*curCateCount/vectorCount;
				double negExp = (vectorCount-posRowSum)*curCateCount/vectorCount;
				chiSquare += (posExp-curCatePosFeatCount)*(posExp-curCatePosFeatCount)/posExp;
				chiSquare += (negExp-(curCateCount-curCatePosFeatCount))*(negExp-(curCateCount-curCatePosFeatCount))/negExp;
			}
			chi.put(curFeat, chiSquare);    //? concurrent modification error?
		}
		
		
		//<calcualte chi square>
		
		
		/*
		for(String curFeat : docFreq.keySet()) {
			
		}*/
		//Map<String, double[]> expect = new HashMap<String, Double>();
		
		return chi;
	}
}
