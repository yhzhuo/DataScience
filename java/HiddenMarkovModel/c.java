import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class c {
	public static String outPath = null;
	public static String debugPath = null;
	
	public static StringBuilder helpq2 = null;
	
	public static String o(Object s) throws IOException {
		if(helpq2 != null) {
			helpq2.append(s.toString()+"\n");
			return s.toString();
		}
		if(outPath != null) {
			write2File(s.toString(), outPath);
		} else {
			System.out.println(s.toString());
		}
		return s.toString();
	}
	
	public static String d(Object s) throws IOException {
		if(debugPath != null) {
			write2File(s.toString(), debugPath);
		} else {
			System.out.println(s.toString());
		}
		return s.toString();
	}
	
	public static String readFile(String file) throws IOException {
		StringBuilder ret = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			ret.append(line+"\n");
		}
		reader.close();
		return ret.toString();
	}
	
	public static void write2File(String content, String file) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		out.println(content);
	    out.close();
	}
}
