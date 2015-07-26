import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class c {
	public static String outPath = null;
	public static String debugPath = null;
	
	public static StringBuilder outBuffer = null;
	public static StringBuilder debugBuffer = null;
	
	public static String o(Object s) throws IOException {
		if(outBuffer != null) {
			outBuffer.append(s.toString()+"\n");
		} else if(outPath != null) {
			write2File(s.toString(), outPath);
		} else {
			System.out.println(s.toString());
		}
		return s.toString();
	}
	
	public static String d(Object s) throws IOException {
		
		if(debugBuffer != null) {
			debugBuffer.append(s.toString()+"\n");
		} else if(debugPath != null) {
			write2File(s.toString(), debugPath);
		} else {
			System.out.println(s.toString());
		}
		
		return s.toString();
	}
	
	public static String o1(Object s) throws IOException {
		if(outBuffer != null) {
			outBuffer.append(s.toString());
		} else if(outPath != null) {
			write2File(s.toString(), outPath);
		} else {
			System.out.print(s.toString());
		}
		return s.toString();
	}
	
	public static String d1(Object s) throws IOException {
		if(debugBuffer != null) {
			debugBuffer.append(s.toString());
		} else if(debugPath != null) {
			write2File(s.toString(), debugPath);
		} else {
			System.out.print(s.toString());
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
