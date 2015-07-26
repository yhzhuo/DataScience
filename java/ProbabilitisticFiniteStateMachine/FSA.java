import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FSA {
	private Graph<String, String> fsa;
	private Set<String> finalStates;
	private String startState;
	
	
	public FSA(String fsaNotation) {
		fsa = new Graph<String, String>();
		finalStates = new HashSet<String>();
		buildFSA(fsaNotation);
	}
	
	private void buildFSA(String fsaNotation) {
		Scanner s = new Scanner(fsaNotation);
		int lineNum = 1;
		while(s.hasNextLine()) {
			String curLine = s.nextLine();
			if(!curLine.contains("(")) {
				finalStates.add(curLine.replace(" ", ""));
			} else {
				String[] parseResult = parseOneLine(curLine);
				if(lineNum == 2) {
					startState = parseResult[0];
				}
				fsa.addNode(parseResult[0]);
				fsa.addNode(parseResult[1]);
				fsa.addEdge(parseResult[0], parseResult[1], parseResult[2]);
			}
			
			lineNum++;
		}
		s.close();
		
	}
	
	private String[] parseOneLine(String oneLine) {
		oneLine = oneLine.replaceAll("\\(", " ");
		oneLine = oneLine.replaceAll("\\)", " ");
		oneLine = oneLine.replaceAll("\"", " ");
		oneLine = oneLine.replaceAll("[\\s]+", " ").trim();	
		String[] ret = oneLine.split(" ");
		return ret;
	}
	
	public boolean accept(String input) {
		input = input.replace("\"", "").trim();
		String[] sequence = input.split(" ");
		Set<String> curStates = new HashSet<String>();
		Set<String> toStates = new HashSet<String>();
		curStates.add(startState);
		
		for(int i = 0; i < sequence.length; i++) {
			if(sequence[i].equals("*e*")) {
				continue;
			}
			curStates = processEmptyTransition(curStates);
			for(String thisCurState : curStates) {
				Map<String, Set<String>> transitTo = fsa.getAllChildren(thisCurState);
				
				for(String child : transitTo.keySet()) {
					Set<String> edges = transitTo.get(child);
					if(edges.contains(sequence[i])) {
						toStates.add(child);
					}
				}
			}
			if(toStates.isEmpty()) {
				return false;
			}
			Set<String> temp = curStates;
			curStates = toStates;
			temp.clear();
			toStates = temp;
			curStates = processEmptyTransition(curStates);
		}
		for(String finalState : finalStates) {
			if(curStates.contains(finalState)) {
				return true;
			}
		}
		return false;
	}
	
	private Set<String> processEmptyTransition(Set<String> curStates) {
		Set<String> newCurStates = new HashSet<String>();
		for(String curState : curStates) {
			newCurStates.add(curState);
			Map<String, Set<String>> children = fsa.getAllChildren(curState);
			for(String child : children.keySet()) {
				if(children.get(child).contains("*e*")) {
					newCurStates.add(child);
				}
			}
		}
		if(newCurStates.size() == curStates.size()) {
			return newCurStates;
		} else {
			return processEmptyTransition(newCurStates);
		}
	}
}
