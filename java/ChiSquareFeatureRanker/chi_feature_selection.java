import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;


public class chi_feature_selection {
	
	// command line: chi_feature_selection <train_vector> <test_vector> <chi_thres> <feature_chi_file> <train_vector_out> <test_vector_out> 
	
	private static String trainFile;
	private static String testFile;
	private static double chiThres;
	private static String chiFile;
	private static String trainOut;
	private static String testOut;
	
	
	private static Set<String> keptFeats;
	public static void main(String[] args) throws IOException {
		//<get param>
		trainFile = args[0];
		testFile = args[1];
		chiThres = Double.parseDouble(args[2]);
		chiFile = args[3];
		trainOut = args[4];
		testOut = args[5];
		//</get param>
		buildFeatSet();
		featSelect(trainFile, trainOut);
		featSelect(testFile, testOut);
	}
	
	private static void buildFeatSet() throws IOException {
		keptFeats = new HashSet<String>();
		BufferedReader br = c.readFileByLine(chiFile);
		String line;
		while((line = br.readLine()) != null) {
			String[] info = line.split(" ");
			double chiValue = Double.parseDouble(info[1]);
			if(chiValue >= chiThres) {
				keptFeats.add(info[0]);
			}
		}
		br.close();
	}
	
	private static void featSelect(String input, String output) throws IOException {
		PrintStream p = new PrintStream(output);
		BufferedReader br = c.readFileByLine(input);
		String line;
		while((line = br.readLine()) != null) {
			String[] info = line.split(" ");
			if(info.length == 0) {
				continue;
			}
			StringBuilder res = new StringBuilder();
			res.append(info[0]+" ");
			for(int i = 1; i < info.length; i++) {
				//String[] feat = info[i].split(":");
				if(keptFeats.contains(info[i].split(":")[0])) {
					res.append(info[i]+" ");
				}
			}
			p.println(res.toString().trim());
		}
		p.close();
		br.close();
	}
}
