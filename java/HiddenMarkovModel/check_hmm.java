import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class check_hmm {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static Map<String, Integer> outLineMap = new HashMap<String, Integer>();
	public static Map<String, List<Double>> tranMap = new HashMap<String, List<Double>>();
	public static Map<String, List<Double>> emiMap = new HashMap<String, List<Double>>();
	public static Map<String, List<Double>> iniMap = new HashMap<String, List<Double>>();
	public static Set<String> probNlogEmi = new HashSet<String>();
	public static Set<String> probNlogTran = new HashSet<String>();
	public static Set<String> probNlogIni = new HashSet<String>();
	public static Set<String> allStates = new HashSet<String>();
	public static final double PRECISION = 0.00001;
	
	public static void main(String[] args) throws IOException {
		//<debug>
		/*
		args = new String[1];
		args[0] = c.readFile("hmm_ex1");
		*/
		//c.outPath = "q3_out";
		//</debug>
		
		//<get input>
		args[0] = c.readFile(args[0]);
		//</get input>
		
		Scanner input = new Scanner(args[0]);
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
			
			if(stage.equals("outline")) {
				String[] res = curLine.split("=");
				outLineMap.put(res[0], Integer.parseInt(res[1]));
				
			} else if(stage.equals("\\init")) {
				String[] res = curLine.split("\\s+");
				addNew(iniMap, res[0], Double.parseDouble(res[1]));
				if(res.length == 3) {
					double logProb = Double.parseDouble(res[2]);
					if(Math.abs(Double.parseDouble(res[1]) - Math.pow(10, logProb)) > PRECISION) {
						probNlogIni.add(res[0]);
					}
				}
			} else if(stage.equals("\\transition")) {
				String[] info = curLine.split("\\s+");
				allStates.add(info[0]);    
				allStates.add(info[1]);          //?: should we count emission state number in state_num? this version consider
				processRelation(curLine, tranMap, probNlogTran);
			} else if(stage.equals("\\emission")) {
				processRelation(curLine, emiMap, probNlogEmi);
			}
		}
		input.close();
		
		//<analyze>
		Map<String, Double> tranResult = analyzeRelation(tranMap, false);
		Map<String, Double> emiResult = analyzeRelation(emiMap, false);
		Map<String, Double> iniResult = analyzeRelation(iniMap, true);
		int tranRealNum = getRelationLineNum(tranMap);
		int emiRealNum = getRelationLineNum(emiMap);
		int iniRealNum = getRelationLineNum(iniMap);
		int stateRealNum = getstate_num();
		//</analyze>
		
		//<output>
		
		//output basic information
		c.o("state_num="+outLineMap.get("state_num"));
		c.o("sym_num="+outLineMap.get("sym_num"));
		
		//output outline
		outOutline("init_line_num", iniRealNum);
		outOutline("trans_line_num", tranRealNum);
		outOutline("emiss_line_num", emiRealNum);
		outOutline("state_num", stateRealNum);
		
		//output tran
		outRelation("trans_prob_sum", tranResult);
		//output emi
		outRelation("emiss_prob_sum", emiResult);
		//output ini
		outRelation("init_prob_sum", iniResult);
		//output tran probNlog
		outProbNlog(probNlogTran, "transion");
		//output emi probNlog
		outProbNlog(probNlogEmi, "emission");
		//output ini probNlog
		outProbNlog(probNlogIni, "start state");
		//</output>
	}
	
	private static void outProbNlog(Set<String> probNlog, String name) throws IOException {
		for(String s : probNlog) {
			c.o("warning: the log_porb of "+name+" "+s+" does not equal to its prob when base=10");
		}
	}
	
	private static void outRelation(String probSumName, Map<String, Double> result) throws IOException {
		for(String key : result.keySet()) {
			if(Math.abs(result.get(key) - 1.0) > PRECISION) {
				c.o("warning: the "+probSumName+" for state "+key+" is "+result.get(key));
			}
		}
	}
	
	private static void outOutline(String feature, int real) throws IOException {
		if(real != outLineMap.get(feature)) {
			c.o("warning: different numbers of "+feature+": claimed="+outLineMap.get(feature)+", real="+real);
		}
	}
	

	
	private static int getstate_num() {
		return allStates.size();
	}
	
	private static int getRelationLineNum(Map<String, List<Double>> relation) {
		int ret = 0;
		Iterator<String> i = relation.keySet().iterator();
		while(i.hasNext()) {
			ret += relation.get(i.next()).size();
		}
		return ret;
	}
	
	private static Map<String, Double> analyzeRelation(Map<String, List<Double>> relation, boolean isIni) {
		Map<String, Double> ret = new HashMap<String, Double>();
		for(String key : relation.keySet()) {
			List<Double> l = relation.get(key);
			Iterator<Double> itr = l.iterator();
			double probSum = 0.0;
			while(itr.hasNext()) {
				probSum += itr.next();
			}
			ret.put(key, probSum);
		}
		if(!isIni) {
			for(String state : allStates) {
				if(!ret.keySet().contains(state)) {
					ret.put(state, 0.0);
				}
			}
		}
		
		return ret;
	}
	
	private static void processRelation(String curLine, Map<String, List<Double>> map, Set<String> probNlog) {
		String[] parse = curLine.split("\\s+");
		String from = parse[0];
		String to = parse[1];
		double prob = Double.parseDouble(parse[2]);
		double logProb = 0.0;
		if(parse.length == 4) {
			logProb = Double.parseDouble(parse[3]);
			if(Math.abs(prob - Math.pow(10, logProb)) > PRECISION) {
				probNlog.add(from+" to "+to);
			}
		}
		addNew(map, from, prob);
	}
	
	private static void addNew(Map<String, List<Double>> curMap, String key, double value) {
		if(curMap.containsKey(key)) {
			curMap.get(key).add(value);
		} else {
			List<Double> l = new LinkedList<Double>();
			l.add(value);
			curMap.put(key, l);
		}
	}
}
