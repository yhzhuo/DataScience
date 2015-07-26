import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class fst_acceptor2 {
	public static void main(String[] args) throws Exception {
		
		args[0] = readFile(args[0]);
		args[1] = readFile(args[1]);
      
		if(args.length != 2) {
			throw new Exception("The number of inputs should be 2");
		}
		
		String fsaNotation = args[0];
		String input = args[1];
		FST fst = new FST(fsaNotation);
		Scanner getEachLine = new Scanner(input);
		while(getEachLine.hasNextLine()){
			String curLine = getEachLine.nextLine();
			List<String> result = fst.accept(curLine);
			if(result != null) {
				System.out.println(curLine+" => "+getResult(result));
			} else {
				System.out.println(curLine+" => *none* 0");
			}
		}
		getEachLine.close();
	}
	
	private static String getResult(List<String> result) {
		Object[] resultArr = result.toArray();
		StringBuilder ret = new StringBuilder();
		for(int i = resultArr.length-2; i >= 0; i--) {
			if(!resultArr[i].toString().equals("")) {
				ret.append("\""+resultArr[i].toString()+"\" ");
			}
		}
		ret.append(resultArr[resultArr.length-1].toString());
		return ret.toString();
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
	
}
