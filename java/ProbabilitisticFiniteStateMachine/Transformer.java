import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class Transformer {
	private Map<String, List<String>> POS2Lexicon;
	private Set<String> finalStates;
	private List<String[]> transitions;
	private String startState;
	int auto = 0;
	
	public Transformer(Scanner inputFSM, Scanner POS2lexiconFile) {
		POS2Lexicon = new HashMap<String, List<String>>();
		finalStates = new HashSet<String>();
		transitions = new LinkedList<String[]>();
		buildPOS2LexiconMap(POS2lexiconFile);
		buildOriginalFSM(inputFSM);
	}
	
	private void buildOriginalFSM(Scanner inputFSM) {
		int count = 0;
		while(inputFSM.hasNextLine()) {
			String curLine = inputFSM.nextLine();
			if(curLine.trim().equals("")) {
				continue;
			}
			curLine = curLine.replace("(", "").trim();
			curLine = curLine.replace(")", "");
			String[] line = curLine.split("\\s+");
			if(line.length == 1) {
				finalStates.add(line[0]);
			} else {
				count++;
				transitions.add(line);
			}
			if(count == 1) {
				startState = line[0];
			}
		}
	}
	
	private void buildPOS2LexiconMap(Scanner POS2lexiconFile) {
		while(POS2lexiconFile.hasNextLine()) {
			String curLine = POS2lexiconFile.nextLine();
			if(curLine.trim().equals("")) {
				continue;
			}
			String[] line = curLine.split("\\s+");
			if(POS2Lexicon.containsKey(line[1])) {
				POS2Lexicon.get(line[1]).add(line[0]);
			} else {
				List<String> temp = new LinkedList<String>();
				temp.add(line[0]);
				POS2Lexicon.put(line[1], temp);
			}
		}
	}
	
	//post: the Object array has three elements, first is start state, second is final states, third is transition arrays
	public Object[] getSplitedFSM(boolean isFST) {
		List<String[]> splitedTran = new LinkedList<String[]>();
		
		for(String[] curTran : transitions) {   //like, curTran[0]: q1, [1]: q2, [2]: irreg_past_verb_form
			if(curTran[2].equals("*e*")) {
				if(isFST) {
					String[] curNewTran = {curTran[0], curTran[1], "*e*", "*e*"};
					splitedTran.add(curNewTran);
				} else {
					String[] curNewTran = {curTran[0], curTran[1], "*e*"};
					splitedTran.add(curNewTran);
				}
				continue;
			}
			List<String> wordTrans = POS2Lexicon.get(curTran[2]);
			for(String wordTran : wordTrans) {
				String from = null;
				String to = null;
				
				for(int i = 0; i < wordTran.length(); i++) {
					String tran = wordTran.substring(i, i+1);
					String out = null;
					if(isFST) {
						out = tran;
					}
					if(i == 0) {
						from = curTran[0];
					} else {
						from = to;
					}
					if(i == wordTran.length()-1) {
						to = curTran[1];
						if(isFST) {
							out += "/"+curTran[2];
						}
					} else {
						to = getSplitState();
					}
					String[] curNewTran = {from, to, tran, out};
					splitedTran.add(curNewTran);
				}
			}
		}
		Object[] ret = {startState, Collections.unmodifiableSet(finalStates), splitedTran};
		return ret;
	}
	
	private String getSplitState() {
		auto++;
		return "self"+auto;
	}
}
