import java.io.IOException;
import java.util.*;
public class viterbi {
	// save before use smallest prob
	private static Map<String, Map<String, Double>> emission;
	private static Map<String, Map<String, Double>> transition;
	private static Map<String, Double> init;
	private static Set<String> allTokens;
	private static final double defUnkProb = 0.00000000000000000000000000000001;
	private static double DOUBLE_PRECISION = 0.00000000000000000000000000000001;
	//private static 
	//private static String inputHMM;
	
	//already ok version
	
	public static void main(String[] args) throws Exception {
		//<debug>
		//c.debugPath = "processing_log";
		//c.debugBuffer = new StringBuilder();
		
		args = new String[3];
		args[0] = "hmm4";
		args[1] = "test.word";
		args[2] = "sys4";
		
		//</debug>
		
		
		
		c.outPath = args[2];
		c.outBuffer = new StringBuilder();
		String inputHMM = c.readFile(args[0]);
		String testData = c.readFile(args[1]);
		buildModel(inputHMM);
		//debug_checkTransition();
		//c.d("all ok!");
		runViterbi(testData);
		c.write2File(c.outBuffer.toString(), c.outPath);
		//c.write2File(c.debugBuffer.toString(), c.debugPath);
	}
	
	//use the key set of emission probability as all states
	private static void runViterbi(String testData) throws IOException {
		Scanner input = new Scanner(testData);
		//List<String[]> ret = new LinkedList<String[]>();
		Map<String, double[]> delta = new HashMap<String, double[]>();
		Map<String, String[]> backPointer = new HashMap<String, String[]>();
		int line = 1;
		while(input.hasNextLine()) {
			c.d("line: "+line);
			line++;
			String curLineText = "<begin> "+input.nextLine().trim();
			String[] curLine = curLineText.split(" ");
			makeMatrix(delta, backPointer, curLine);
			
			//<real stuff>
			for(String curState : delta.keySet()) {
				/*
				double initProb = 0.0;
				if(init.containsKey(curState)) {
					initProb = init.get(curState);
				}
				*/
				double initProb = getProb(init, curState);
				/*
				double emitProb = 0.0;
				if(emission.get(curState).containsKey(curLine[0])) {
					emitProb = emission.get(curState).get(curLine[0]);
				}
				*/
				
				double emitProb = getProb(emission.get(curState), curLine[0]);
				delta.get(curState)[0] = Math.log10(initProb)+Math.log10(emitProb);
				//c.d("delta.get(curState)[0]: curState: "+curState+ ": "+delta.get(curState)[0]);
			}
			//<debug>
			//Set<Integer> dset = new HashSet<Integer>();
			//</debug>
			for(int i = 1; i < curLine.length; i++) {    //curLine.length
				c.d("now i: "+i);
				for(String curState : emission.keySet()) {
					//<get prob and back>
					double maxProb = Double.NEGATIVE_INFINITY;
					String bestPointer = null;
					for(String preState : emission.keySet()) {
						//c.d("preState: "+preState+" curState: "+curState+" curLine["+i+"]: "+curLine[i]);
						double tranProb = getProb(transition.get(preState), curState); //transition.get(preState).get(curState);
						
						double emiProb = getProb(emission.get(curState), curLine[i]);//emission.get(curState).get(curLine[i]);
						if(emiProb < DOUBLE_PRECISION) {
							emiProb = defUnkProb;
						}
						double totalProb = Math.log10(tranProb)+Math.log10(emiProb)+delta.get(preState)[i-1];
						//<debug>
						/*
						if(totalProb > Double.NEGATIVE_INFINITY && !dset.contains(i)) {
							
							c.d("curLine.length = "+curLine.length);
							dset.add(i);
							c.d("preState: "+preState+" curState: "+curState+" curLine["+i+"]: "+curLine[i]+" tranProb: "+tranProb+" emiProb: "+emiProb+" totalProb: "+totalProb);
						}
						if(i == 6 && preState.equals("WP$_NN") && curState.split("_")[0].equals("NN")) {
							c.d("===equals 6!!!===");
							c.d("prob: WP$_NN	NN_CC: "+transition.get("WP$_NN").get("NN_CC"));
							c.d("prob: WP$_NN	NN_VBZ: "+transition.get("WP$_NN").get("NN_VBZ"));
							//c.d("curLine.length = "+curLine.length);
							c.d("preState: "+preState+" curState: "+curState+" curLine["+i+"]: "+curLine[i]+" tranProb: "+tranProb+" emiProb: "+emiProb+" totalProb: "+totalProb);
						}
						*/
						//</debug>
						if(totalProb > maxProb) {    //!: > and >= has extermely great difference
							maxProb = totalProb;
							bestPointer = preState;
							//c.d("bestPointer: "+bestPointer);
							//c.d("totalProb: "+totalProb);
							
						}
					}
					//c.d("finally best pointer: "+bestPointer);
					delta.get(curState)[i] = maxProb;
					backPointer.get(curState)[i] = bestPointer;
					//</get prob and back>
				}
			}
			
			String bestFinalState = null;
			double maxProb = Double.NEGATIVE_INFINITY;   //may have problems here
			for(String curState : emission.keySet()) {    //may have problems for variable names
				double[] curProbArr = delta.get(curState);  //may have problems for variable names
				//<debug>
				/*
				c.d("curState: "+curState);
				for(double cur : curProbArr) {
					c.d1(cur+" ");
				}
				String[] curStrArr = backPointer.get(curState);
				for(String cur : curStrArr) {
					c.d1(cur+" ");
				}
				c.d("======");
				*/
				//</debug>
				if(maxProb < curProbArr[curProbArr.length-1]) {   //curProbArr.length-1
					maxProb = curProbArr[curProbArr.length-1];   //curProbArr.length-1
					bestFinalState = curState;
				}
			}
			String curBackState = bestFinalState;
			String[] backList = backPointer.get(bestFinalState);
			StringBuilder out = new StringBuilder();
			//<get original text>
			int blankIndex = curLineText.indexOf(" ");
			out.append(curLineText.substring(blankIndex+1, curLineText.length())+" => ");
			//</get original text>
			int backCount = backList.length - 1;//backList.length - 1;
			Stack<String> reverse = new Stack<String>();
			reverse.push(curBackState);
			while(curBackState != null) {
				reverse.push(backList[backCount]);
				curBackState = backList[backCount];
				backList = backPointer.get(curBackState);
				backCount--;
			}
			if(reverse.size() >= 2) {
				reverse.pop();    // kick out BOS_BOS and null
				reverse.pop();
			}
			while(!reverse.isEmpty()) {
				out.append(reverse.pop()+" ");
			}
			out.append(maxProb);
			c.o(out.toString());
			/*
			String[] result = backPointer.get(bestFinalState);
			//<output solution for current line>
			StringBuilder out = new StringBuilder();
			out.append(curLineText+" => ");
			
			//for(String cur : result) {
			//	out.append(cur+" ");
			//}
			
			for(int i = 1; i < result.length; i++) {
				out.append(result[i]+" ");
			}
			out.append(bestFinalState+" ");
			out.append(maxProb);
			c.o(out.toString());
			*/
			//</output solution for current line>
			
			
			//ret.add(backPointer.get(bestFinalState));
			//</real stuff>
		}
		input.close();
		//return ret;
	}
	
	
	private static double getProb(Map<String, Double> map, String key) {
		double ret = 0.0;
		if(map != null && map.containsKey(key)) {   //may have problems here
			ret = map.get(key);
		}
		return ret;
	}
	
	private static void makeMatrix(Map<String, double[]> delta, Map<String, String[]> backPointer, String[] curLine) {
		for(String curState : emission.keySet()) {     // if no emission probability it will be meaningless even if it has transition probability
			delta.put(curState, new double[curLine.length]);
			backPointer.put(curState, new String[curLine.length]);
		}
		for(int i = 0; i < curLine.length; i++) {
			if(!allTokens.contains(curLine[i])) {      // may have problems here!!! containsValue is a unfarmiliar method
				curLine[i] = "<unk>";    //?: or < unk >
			}
		}
	}
	
	private static void buildModel(String inputHMM) throws IOException {
		//<basic transition emission>
		emission = new HashMap<String, Map<String, Double>>();
		transition = new HashMap<String, Map<String, Double>>();
		allTokens = new HashSet<String>();
		
		init = new HashMap<String, Double>();
		
		
		Scanner input = new Scanner(inputHMM);
		String stage = "outline";
		while(input.hasNextLine()) {
			String curLine = input.nextLine().trim();
			if(curLine.equals("")) {
				continue;
			}
			if(curLine.equals("\\init") || 
				curLine.equals("\\transition") ||
				curLine.equals("\\emission")) {
				stage = curLine;
				continue;
			}
			String[] curInfo = curLine.split("\\s+");
			if(stage.equals("\\transition")) {
				
				if(curInfo[1].split("_")[1].equals("EOS")) {
					continue;
				}
				addRelation(curInfo[0], curInfo[1], curInfo[2], transition);
			} else if(stage.equals("\\emission")) {
				
				
				if(curInfo[1].equals("<end>")) {
					continue;
				}
				addRelation(curInfo[0], curInfo[1], curInfo[2], emission);
				allTokens.add(curInfo[1]);
			} else if(stage.equals("\\init")) {
				
				//c.d("curInfo[0]"+curInfo[0]);
				//c.d("curInfo[1]"+curInfo[1]);
				init.put(curInfo[0], Double.parseDouble(curInfo[1]));
			}
		}
		input.close();
		//</basic transition emission>
		//<add special unk>
		for(String key : emission.keySet()) {
			Map<String, Double> curMap = emission.get(key);
			if(!curMap.containsKey("<unk>")) {
				curMap.put("<unk>", 0.2);
			}
		}
		//</add special unk>
	}
	
	private static void addRelation(String pre, String post, String strProb, Map<String, Map<String, Double>> relation) {
		double prob = Double.parseDouble(strProb);
		if(!relation.containsKey(pre)) {
			relation.put(pre, new HashMap<String, Double>());
		}
		Map<String, Double> postMap = relation.get(pre);
		postMap.put(post, prob);   //this way may have logic problem, because it only accept last realtion
	}
}
