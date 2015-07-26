import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class expand_fsm {

	/**
	 * @param args: args[0] is lexicon_ex; args[1] is original FSM; args[2] is path of output; arg[3] is if print FST
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		  
		if(args.length != 4) {
			throw new Exception("length of args should be 4");
		}
		Scanner POS2lexiconFile = new Scanner(new File(args[0]));
		Scanner inputFSM = new Scanner(new File(args[1]));
		PrintStream ps = new PrintStream(args[2]);
		String isFSTIn = args[3];
		boolean isFST = false;
		if(isFSTIn.equals("1")) {
			isFST = true;
		}
		Transformer tran = new Transformer(inputFSM, POS2lexiconFile);
		Object[] result = null;
		result = tran.getSplitedFSM(isFST);
		String startState = (String)result[0];
		Set<String> finalStates = (Set<String>)result[1];
		List<String[]> trans = (List<String[]>)result[2];
		for(String cur : finalStates) {
			ps.println(cur);
		}
		Iterator<String[]> i = trans.iterator();
		while(i.hasNext()) {
			String[] curLine = i.next();
			if(curLine[0].equals(startState)) {
				printLine(curLine, ps, isFST);
				i.remove();
				break;
			}
		}
		for(String[] curLine : trans) {
			printLine(curLine, ps, isFST);
		}
	}
	
	private static void printLine(String[] line, PrintStream ps, boolean isFST) {
		
		if(!isFST) {
			ps.println("("+line[0]+" ("+line[1]+" "+line[2]+"))");
		} else {
			ps.println("("+line[0]+" ("+line[1]+" "+line[2]+" "+line[3]+"))");
		}
	}
	
	public static String debug(Object s) {
		System.out.println(s);
		return s.toString();
	}
}
