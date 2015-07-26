import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FST {
	private Graph<String, String> fsa;
	private Set<String> finalStates;
	private String startState;
	private Map<String, Set<String>> outProb;
	
	private class Node {
		public String name;
		public Node parent;
		public String parentName;
		public double prob;   // the probability from parent to this node
		public String out;    // the output from parent to this node
		
		public Node() {
			this(startState, null, null, 1.0, "*e*");
		}
		
		public Node(String name, Node parent, String parentName, double prob, String out) {
			this.name = name;
			this.parent = parent;
			this.parentName = parentName;
			this.prob = prob;
			this.out = out;
		}
	}
	
	
	public FST(String fsaNotation) {
		fsa = new Graph<String, String>();
		finalStates = new HashSet<String>();
		outProb = new HashMap<String, Set<String>>();
		buildFST(fsaNotation);
	}
	
	private void buildFST(String fsaNotation) {
		Scanner s = new Scanner(fsaNotation);
		int lineNum = 1;
		while(s.hasNextLine()) {
			String curLine = s.nextLine();
			if(!curLine.contains("(")) {
				curLine = curLine.replace(" ", "");
				finalStates.add(curLine.substring(0, curLine.length()));
			} else {
				String[] parseResult = parseOneLine(curLine);
				if(lineNum == 2) {
					startState = parseResult[0];
				}
				fsa.addNode(parseResult[0]);
				fsa.addNode(parseResult[1]);
				fsa.addEdge(parseResult[0], parseResult[1], parseResult[2]);
				String probKey = parseResult[0]+":"+parseResult[2]+":"+parseResult[1];
				String prob = "1.0";
				if(parseResult.length != 4) {
					prob = parseResult[4];
				}
				String out = parseResult[3];
				if(outProb.containsKey(probKey)) {
					outProb.get(probKey).add(out+":"+prob);
				} else {
					Set<String> cur = new HashSet<String>();
					cur.add(out+":"+prob);
					outProb.put(probKey, cur);
				}
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
		//oneLine = oneLine.replaceAll("[^a-zA-Z*0-9.]", " ");
		
		String[] ret = oneLine.split(" "); // may have problems here
		return ret;
	}
	
	private void processEmptyTransition(List<Node> curStates) {
		//int curStatesSize = curStates.size();
		List<Node> temp = new LinkedList<Node>();
		for(Node curState : curStates) {
			establish("*e*", curState, temp);	
		}
		Iterator<Node> i = temp.iterator();
		boolean noNew = true;
		while(i.hasNext()) {
			Node tempNode = i.next();
			if(!listContains(curStates, tempNode)) {
				curStates.add(tempNode);
				noNew = false;
			}
			
		}
		if(noNew) {
			return;
		} else {
			processEmptyTransition(curStates);
		}
	}
	
	private boolean listContains(List<Node> states, Node node) {
		for(Node cur : states) {
			if(cur.name.equals(node.name)) {
				return true;
			}
		}
		return false;
	}
	
	
	private void establish(String edgeConsidered, Node curState, List<Node> temp) {
		Map<String, Set<String>> children = fsa.getAllChildren(curState.name);
		for(String child : children.keySet()) {
			Set<String> edges = children.get(child);
			if(edges.contains(edgeConsidered)) {
				//set curState as child's parent
				Set<String> transition = outProb.get(curState.name+":"+edgeConsidered+":"+child);
				for(String outAndProb : transition) {
					String out = outAndProb.split(":")[0];
					double prob = Double.parseDouble(outAndProb.split(":")[1]);
					Node newState = new Node(child, curState, curState.name, prob, out);
					temp.add(newState);
				}
			}
		}
	}
	
	public List<String> accept(String input) {
		input = input.replace("\"", "").trim();
		String[] sequence = input.split(" ");
		// two round check if there are empty transition, another round goes toward
		List<Node> curStates = new ArrayList<Node>();
		List<Node> toStates = new ArrayList<Node>();
		Node root = new Node();
		curStates.add(root);
		for(int i = 0; i < sequence.length; i++) {
			processEmptyTransition(curStates);
			//<real transition stuff>
			Iterator<Node> itr = curStates.iterator();
			//List<Node> temp = new LinkedList<Node>();
			while(itr.hasNext()) {
				Node curState = itr.next();
				establish(sequence[i], curState, toStates);
			}
			if(toStates.isEmpty()) {
				return null;
			}
			List<Node> temp = curStates;
			curStates = toStates;
			temp.clear();
			toStates = temp;
			//</real transition stuff>
			processEmptyTransition(curStates);
		}
		// check if any curStates after processing is in the finalStates
		Iterator<Node> itrForOut = curStates.iterator();
		while(itrForOut.hasNext()) {
			Node curState = itrForOut.next();
			if(finalStates.contains(curState.name)) {
				//itrForOut.remove();    // this may have problems
				return processResult(curStates);
			}
		}

		return null;
		
	}
	
	private List<String> processResult(List<Node> curStates) {
		double maxProb = -1.0;
		List<String> bestPath = null;
		for(int i = 0; i < curStates.size(); i++) {
			Node cur = curStates.get(i);
			if(finalStates.contains(cur.name)) {
				List<String> curPath = new LinkedList<String>();
				double curProb = 1.0;
				while(cur.parent != null) {
					curProb *= cur.prob;
					curPath.add(cur.out);
					cur = cur.parent;
				}
				if(curProb > maxProb) {
					maxProb = curProb;
					bestPath = curPath;
				}
			}
		}
		bestPath.add(""+maxProb);
		return bestPath;
	}
}
