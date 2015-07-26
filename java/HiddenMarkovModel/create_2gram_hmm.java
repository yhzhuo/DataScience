import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class create_2gram_hmm {
	public static void main(String args[]) throws IOException {
		//args[0]: the input file, get by cat, assume to have multiple lines
		//String[] input = getInput(args[0]);
		
		
		//<debug>
		/*
		args = new String[1];
		args[0] = c.readFile("wsj_sec0.word_pos");
		Scanner scanInput = new Scanner(args[0]);
		*/
		//</debug>
		
		
		//<get input>
		c.outPath = args[0];
		Scanner scanInput = new Scanner(System.in);
		//</get input>
		
		
		
		Map<String, Map<String, Double>> emission = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Double>> transition = new HashMap<String, Map<String, Double>>();
		Set<String> outSymbols = new HashSet<String>();
		//<build count>
		while(scanInput.hasNextLine()) {
			String[] curLine = getInput(scanInput.nextLine());
			for(int i = 0; i < curLine.length; i++) {
				addEmission(curLine[i], emission, outSymbols);
				if(i == 0) {
					addTransition("BOS", splitT(curLine[i])[1], transition);
				} 
				if(i == curLine.length - 1) {
					addTransition(splitT(curLine[i])[1], "EOS", transition);
				} else {
					addTransition(splitT(curLine[i])[1], splitT(curLine[i+1])[1], transition);
				}
			}
		}
		//</build count>
		scanInput.close();
		count2Prob(emission);
		count2Prob(transition);
		//<output>
		c.o("state_num="+(transition.keySet().size()+1));   // plus 1 for EOS case
		c.o("sym_num="+outSymbols.size());
		c.o("init_line_num=1");  //this is pre-defined since there is only a BOS
		c.o("trans_line_num="+getLineNumber(transition));
		c.o("emiss_line_num="+(getLineNumber(emission)+2));  //note: plus 2 to count for BOS and EOS
		c.o("");
		c.o("\\init");
		c.o("BOS\t1.0\t0.0");
		c.o("");
		c.o("");
		c.o("");
		c.o("\\transition");
		outputRelation(transition);
		c.o("");
		c.o("\\emission");
		c.o("BOS\t<s>\t1.0\t0.0");
		c.o("EOS\t</s>\t1.0\t0.0");
		outputRelation(emission);
		//</output>
		
	}
	
	private static String[] splitT(String s) {
		String cur = s.replaceAll("(.*)(/)(.*)", "$1 $3");
		String[] ret = cur.split(" ");
		ret[0] = ret[0].replace("\\/", "/");
		return ret;
	}
	
	private static void outputRelation(Map<String, Map<String, Double>> relation) throws IOException {
		for(String pre : relation.keySet()) {
			Map<String, Double> postMap = relation.get(pre);
			for(String post : postMap.keySet()) {
				double prob = postMap.get(post);
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
