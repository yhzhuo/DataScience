import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class create_3gram_hmm {
	private static double l1;
	private static double l2;
	private static double l3;
	private static Map<String, Double> unk;
	
	private static Set<String> allStates = new HashSet<String>();
	
	public static void main(String args[]) throws IOException {
		//args[0]: the input file, get by cat, assume to have multiple lines
		//String[] input = getInput(args[0]);
		c.outBuffer = new StringBuilder();
		
		//<debug>
		/*
		c.outPath = "q2_out";
		args = new String[5];
		args[0] = c.readFile("wsj_sec0.word_pos");
		args[1] = "0.1";
		args[2] = "0.1";
		args[3] = "0.8";
		l1 = Double.parseDouble(args[1]);
		l2 = Double.parseDouble(args[2]);
		l3 = Double.parseDouble(args[3]);
		args[4] = c.readFile("unk_prob_sec22");
		Scanner scanInput = new Scanner(args[0]);  //debug
		Scanner scanUnk = new Scanner(args[4]);
		*/
		//</debug>
		

		//<get input>
		
		c.outPath = args[0];
		l1 = Double.parseDouble(args[1]);
		l2 = Double.parseDouble(args[2]);
		l3 = Double.parseDouble(args[3]);
		Scanner scanUnk = new Scanner(c.readFile(args[4]));
		Scanner scanInput = new Scanner(System.in);
		
		//</get input>
		
		
		
		unk = new HashMap<String, Double>();
		while(scanUnk.hasNextLine()) {
			String[] curLine = scanUnk.nextLine().split(" ");
			unk.put(curLine[0], Double.parseDouble(curLine[1]));
		}
		scanUnk.close();
		
		
		
		Map<String, Map<String, Double>> emission = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Double>> transition = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Double>> triTran = new HashMap<String, Map<String, Double>>();
		Map<String, Double> uniTran = new HashMap<String, Double>();
		Set<String> outSymbols = new HashSet<String>();
		//<build count>
		while(scanInput.hasNextLine()) {
			String[] curLine = getInput("<begin>/BOS "+scanInput.nextLine()+" <end>/EOS");
			for(int i = 0; i < curLine.length; i++) {
				if(i < curLine.length - 1) {
					addEmission(splitT(curLine[i])[1]+"_"+splitT(curLine[i+1])[1], splitT(curLine[i+1])[0], emission, outSymbols);
				}
				String tag = splitT(curLine[i])[1];
				if(uniTran.containsKey(tag)) {
					uniTran.put(tag, uniTran.get(tag)+1);
				} else {
					uniTran.put(tag, 1.0);
				}
				
				//<for bigram>
				if(i < curLine.length - 1) {
					addTransition(splitT(curLine[i])[1], splitT(curLine[i+1])[1], transition);
				}
				
				
				//</for bigram>
				
				//<for trigram>
				if(i < curLine.length - 2) {
					addTransition(splitT(curLine[i])[1]+"_"+splitT(curLine[i+1])[1], splitT(curLine[i+1])[1]+"_"+splitT(curLine[i+2])[1], triTran);
				}
				
				//</for trigram>
			}
		}
		//</build count>
		scanInput.close();
		count2Prob(emission);
		emiAddUnk(emission);
		count2Prob(triTran);
		count2Prob(transition);
		count2ProbUni(uniTran);
		//<output>
		
		c.o("sym_num="+outSymbols.size());
		c.o("init_line_num=1");  //this is pre-defined
		c.o("trans_line_num="+(getLineNumber(triTran)+getBOSLineNumber(transition)));
		c.o("emiss_line_num="+(getLineNumber(emission)+1));  //note: plus one to count for BOS	<t>
		c.o("");
		c.o("\\init");
		c.o("BOS_BOS\t1.0\t0.0");
		c.o("");
		c.o("");
		c.o("");
		c.o("\\transition");
		outputTransition(triTran, transition, uniTran);
		c.o("");
		c.o("\\emission");
		c.o("BOS_BOS\t<begin>\t1.0\t0.0");
		outputEmission(emission);
		c.write2File("state_num="+(allStates.size()), c.outPath);
		c.write2File(c.outBuffer.toString(), c.outPath);
		//</output>
		
	}
	
	//pre: the emission map has already finished count2Prob, unk is ready
	//post: add unk probability for each tags and change the original probability
	private static void emiAddUnk(Map<String, Map<String, Double>> emission) {
		/*
		String curPre = null;
		double curUnkProb = 0.0;
		*/
		for(String key : emission.keySet()) {
			/*
			if(!key.equals(curPre)) {
				curPre = key;
				String curEmiState = key.split("_")[1];
				if(unk.containsKey(curEmiState)) {
					curUnkProb = unk.get(curEmiState);
				} else {
					continue;   // may have problems here
				}
				
			}
			*/
			String curEmi = key.split("_")[1];
			if(unk.containsKey(curEmi)) {
				double curUnkProb = unk.get(curEmi);
				Map<String, Double> tag2Word = emission.get(key);
				for(String word : tag2Word.keySet()) {
					tag2Word.put(word, tag2Word.get(word)*(1-curUnkProb));
				}
				tag2Word.put("<unk>", curUnkProb);
			}
			
			/*
			String emiState = curPre.split("_")[1];
			double curUnkProb = unk.get(emiState);
			*/
			
		}
	}
	
	private static int getBOSLineNumber(Map<String, Map<String, Double>> biTran) {
		return biTran.get("BOS").keySet().size();
	}
	
	private static void count2ProbUni(Map<String, Double> uniTran) throws IOException {
		double total = 0.0;
		Iterator<String> i = uniTran.keySet().iterator();
		while(i.hasNext()) {
			total += uniTran.get(i.next());
		}
		for(String cur : uniTran.keySet()) {
			uniTran.put(cur, uniTran.get(cur)/total);
		}
		probSumUpTo1("uniTran", uniTran);
	}
	
	private static String[] splitT(String s) {
		String cur = s.replaceAll("(.*)(/)(.*)", "$1 $3");
		String[] ret = cur.split(" ");
		ret[0] = ret[0].replace("\\/", "/");
		return ret;
	}
	
	private static void outputTransition(Map<String, Map<String, Double>> triTran, Map<String, Map<String, Double>> biTran, Map<String, Double> uniTran) throws IOException {
		//normal process
		for(String pre : triTran.keySet()) {
			Map<String, Double> postMap = triTran.get(pre);
			for(String post : postMap.keySet()) {
				double triProb = postMap.get(post);
				//<get biProb>
				
				double biProb = biTran.get(pre.split("_")[1]).get(post.split("_")[1]);
				//</get biProb>
				double uniProb = uniTran.get(post.split("_")[1]);
				double finalProb = l3*triProb+l2*biProb+l1*uniProb;
				allStates.add(pre);
				allStates.add(post);
				c.o(pre+"\t"+post+"\t"+finalProb+"\t"+Math.log10(finalProb));
			}
		}
		//output BOS
		Map<String, Double> bos = biTran.get("BOS");
		for(String key : bos.keySet()) {
			double prob = bos.get(key);
			double finalProb = l3*prob+l2*prob+l1*uniTran.get(key);
			c.o("BOS_BOS"+"\t"+"BOS_"+key+"\t"+finalProb+"\t"+Math.log10(finalProb));
		}
		allStates.add("BOS_BOS");
	}
	
	private static void outputEmission(Map<String, Map<String, Double>> emission) throws IOException {
		for(String pre : emission.keySet()) {
			Map<String, Double> postMap = emission.get(pre);
			for(String post : postMap.keySet()) {
				double prob = postMap.get(post);
				
				/*
				Double unkProb = unk.get(pre.split("_")[1]);
				if(unkProb != null) {
					c.d("goes into the unk part");
					prob *= (1-unkProb);
				}
				*/
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
	
	
	private static void count2Prob(Map<String, Map<String, Double>> relation) throws IOException {
		for(String curPre : relation.keySet()) {
			Map<String, Double> toMap = relation.get(curPre);
			double totalCount = 0.0;
			for(String curTo : toMap.keySet()) {
				totalCount += toMap.get(curTo);
			}
			for(String curTo2 : toMap.keySet()) {
				toMap.put(curTo2, toMap.get(curTo2)/totalCount);
			}
			probSumUpTo1("toMap", toMap);
		}
	}
	
	private static void addEmission(String state, String token, Map<String, Map<String, Double>> emission, Set<String> outSymbols) {
		outSymbols.add(token);
		addNewRelation(state, token, emission);
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
	
	private static void probSumUpTo1(String name, Map<String, Double> map) throws IOException {
		double total = 0.0;
		Iterator<String> itr = map.keySet().iterator();
		while(itr.hasNext()) {
			total += map.get(itr.next());
		}
		if(Math.abs(total - 1.0) > 0.000001) {
			c.d(name+" totalprob: "+total);
		}
		if(Math.abs(l1+l2+l3 - 1.0) > 0.000001) {
			c.d("total lamada: "+(l1+l2+l3));
		}
	}

}
