import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
// save before change to two tag states

public class create_3gram_hmm {
	public static double l1;
	public static double l2;
	public static double l3;
	public static Map<String, Double> unk;
	public static void main(String args[]) throws IOException {
		//args[0]: the input file, get by cat, assume to have multiple lines
		//String[] input = getInput(args[0]);
		
		
		//<debug>
		args = new String[5];
		args[0] = c.readFile("wsj_sec0.word_pos");
		args[1] = "0.1";
		args[2] = "0.1";
		args[3] = "0.8";
		args[4] = c.readFile("unk_prob_sec22");
		//</debug>
		
		//<process input>
		l1 = Double.parseDouble(args[1]);
		l2 = Double.parseDouble(args[2]);
		l3 = Double.parseDouble(args[3]);
		Scanner scanUnk = new Scanner(args[4]);
		unk = new HashMap<String, Double>();
		while(scanUnk.hasNextLine()) {
			String[] curLine = scanUnk.nextLine().split(" ");
			unk.put(curLine[0], Double.parseDouble(curLine[1]));
		}
		scanUnk.close();
		//</process input>
		
		Scanner scanInput = new Scanner(args[0]);
		Map<String, Map<String, Double>> emission = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Double>> transition = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Double>> triTran = new HashMap<String, Map<String, Double>>();
		Map<String, Double> uniTran = new HashMap<String, Double>();
		Set<String> outSymbols = new HashSet<String>();    //? is symbol number of type tokens or number of tokens?
		//transition.put("BOS", new HashMap<String, Integer>());
		//<build count>
		while(scanInput.hasNextLine()) {
			String[] curLine = getInput(scanInput.nextLine());
			for(int i = 0; i < curLine.length; i++) {
				
				addEmission(curLine[i], emission, outSymbols);
				
				
				//</for unigram>
				String tag = splitT(curLine[i])[1];
				if(uniTran.containsKey(tag)) {
					uniTran.put(tag, uniTran.get(tag)+1);
				} else {
					uniTran.put(tag, 1.0);
				}
				//</for unigram>
				
				
				
				//<for bigram>
				if(i == 0) {
					addTransition("BOS", splitT(curLine[i])[1], transition);
				} 
				if(i == curLine.length - 1) {
					addTransition(splitT(curLine[i])[1], "EOS", transition);
				} else {
					addTransition(splitT(curLine[i])[1], splitT(curLine[i+1])[1], transition);
				}
				//</for bigram>
				
				
				
				//<for trigram>
				if(i <= curLine.length-3) {
					if(i == 0) {
						addTransition("BOS_"+splitT(curLine[i])[1], splitT(curLine[i+1])[1], triTran);
					}
					if(i == curLine.length - 2) {
						addTransition(splitT(curLine[i])[1]+"_"+splitT(curLine[i+1])[1], "EOS", triTran);
					} else {
						addTransition(splitT(curLine[i])[1]+"_"+splitT(curLine[i+1])[1], splitT(curLine[i+2])[1], triTran);
					}
				}
				//</for trigram>
				
			}
		}
		//</build count>
		scanInput.close();
		count2Prob(emission);
		count2Prob(triTran);
		count2ProbUni(uniTran);
		//<output>
		c.o("state_num="+(triTran.keySet().size()+transition.keySet().size()));   //?: should we count emission state number in state_num? this version consider
		c.o("sym_num="+outSymbols.size());
		c.o("init_line_num=1");  //this is pre-defined
		c.o("trans_line_num="+getLineNumber(triTran));
		c.o("emiss_line_num="+(getLineNumber(emission)+2));  //note: plus one to count for BOS	<t>
		c.o("");
		c.o("\\init");
		c.o("BOS\t1.0\t0.0");
		c.o("");
		c.o("");
		c.o("");
		c.o("\\transition");
		outputTransition(triTran, transition, uniTran);
		c.o("");
		c.o("\\emission");
		c.o("BOS\t<s>\t1.0\t0.0");
		c.o("EOS\t</s>\t1.0\t0.0");
		outputEmission(emission);
		//System.out.println("get in!!!");
		//</output>
		
	}
	
	private static void count2ProbUni(Map<String, Double> uniTran) {
		double total = 0.0;
		Iterator<String> i = uniTran.keySet().iterator();
		while(i.hasNext()) {
			total += uniTran.get(i.next());
		}
		for(String cur : uniTran.keySet()) {
			uniTran.put(cur, uniTran.get(cur)/total);
		}
	}
	
	private static String[] splitT(String s) {
		String cur = s.replaceAll("(.*)(/)(.*)", "$1 $3");
		String[] ret = cur.split(" ");
		ret[0] = ret[0].replace("\\/", "/");
		return ret;
	}
	
	private static void outputTransition(Map<String, Map<String, Double>> triTran, Map<String, Map<String, Double>> biTran, Map<String, Double> uniTran) throws IOException {
		for(String pre : triTran.keySet()) {
			Map<String, Double> postMap = triTran.get(pre);
			for(String post : postMap.keySet()) {
				double triProb = postMap.get(post);
				//<get biProb>
				
				double biProb = biTran.get(pre.split("_")[1]).get(post);
				//</get biProb>
				double uniProb = uniTran.get(post);
				double finalProb = l3*triProb+l2*biProb+l1*uniProb;
				c.o(pre+"\t"+post+"\t"+finalProb+"\t"+Math.log10(finalProb));
			}
		}
	}
	
	private static void outputEmission(Map<String, Map<String, Double>> emission) throws IOException {
		for(String pre : emission.keySet()) {
			Map<String, Double> postMap = emission.get(pre);
			for(String post : postMap.keySet()) {
				double prob = postMap.get(post);
				//c.d(pre);
				Double unkProb = unk.get(pre);
				if(unkProb != null) {
					prob *= (1-unkProb);
				}
				
				c.o(pre+"\t"+post+"\t"+prob+"\t"+Math.log10(prob));
			}
		}
	}
	
	private static void outputRelation(Map<String, Map<String, Double>> relation) throws IOException {
		//c.d(String.format("%.2f", 12.284));
		//Iterator<String> i = relation.keySet().iterator();
		for(String pre : relation.keySet()) {
			Map<String, Double> postMap = relation.get(pre);
			for(String post : postMap.keySet()) {
				double prob = postMap.get(post);
				//System.out.printf("%-10s %-10.3f", "aaaaa", 124.000);
				//System.out.printf("%-8s %-8s %f", pre, post, prob);
				//c.o("")
				c.o(pre+"\t"+post+"\t"+prob+"\t"+Math.log10(prob));
			}
		}
	}
	
	private static int getLineNumber(Map<String, Map<String, Double>> relation) {
		int ret = 0;
		Iterator<String> i = relation.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			ret += relation.get(key).keySet().size();
		}
		return ret;
	}
	
	
	private static void count2Prob(Map<String, Map<String, Double>> relation) {
		for(String curPre : relation.keySet()) {
			Map<String, Double> toMap = relation.get(curPre);
			double totalCount = 0.0;   // this method may have problems 
			for(String curTo : toMap.keySet()) {
				totalCount += toMap.get(curTo);
			}
			for(String curTo2 : toMap.keySet()) {
				toMap.put(curTo2, toMap.get(curTo2)/totalCount);
			}
		}
	}
	
	private static void addEmission(String tokenATag, Map<String, Map<String, Double>> emission, Set<String> outSymbols) {
		String token = splitT(tokenATag)[0];
		outSymbols.add(token);
		String tag = splitT(tokenATag)[1];
		addNewRelation(tag, token, emission);
		/*
		if(emission.containsKey(tag)) {
			Map<String, Integer> curTagEmi = emission.get(tag);
			curTagEmi.put(token, curTagEmi.get(token)+1);
		} else {
			Map<String, Integer> newTagEmi = new HashMap<String, Integer>();
			newTagEmi.put(token, 1);
			emission.put(tag, newTagEmi);
		}
		*/
	}
	
	private static void addNewRelation(String pre, String post, Map<String, Map<String, Double>> relation) {
		if(relation.containsKey(pre)) {
			Map<String, Double> curTagEmi = relation.get(pre);
			if(curTagEmi.containsKey(post)) {
				curTagEmi.put(post, curTagEmi.get(post)+1);
			} else {
				curTagEmi.put(post, 1.0);
			}
			curTagEmi.put(post, curTagEmi.get(post)+1);
		} else {
			Map<String, Double> newTagEmi = new HashMap<String, Double>();
			newTagEmi.put(post, 1.0);
			relation.put(pre, newTagEmi);
		}
	}
	
	private static void addTransition(String from, String to, Map<String, Map<String, Double>> transition) {
		addNewRelation(from, to, transition);
	}
	
	private static String[] getInput(String input) {
		return input.split(" ");
	}

}
