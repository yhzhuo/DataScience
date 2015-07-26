import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class FSAMain {
	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			throw new Exception("The number of inputs should be 2");
		}
		args[0] = readFile(args[0]);
		args[1] = readFile(args[1]);
		
		String fsaNotation = args[0];
		String input = args[1];
		FSA fsa = new FSA(fsaNotation);
		Scanner getEachLine = new Scanner(input);
		while(getEachLine.hasNextLine()){
			String curLine = getEachLine.nextLine();
			String realInput = curLine.replace("", " ").trim();
			if(fsa.accept(realInput)) {
				System.out.println(curLine+" => yes");
			} else {
				System.out.println(curLine+" => no");
			}
		}
		getEachLine.close();
	}
	
	
	public static String readFile(String file) throws IOException {
		String ret = "";
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
		    ret += line+"\n";
		}
		reader.close();
		return ret;
	}
	
	public static String debug(Object s) {
		System.out.println(s.toString());
		return s.toString();
	}
}
