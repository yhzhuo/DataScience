import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;


public class TokProbCount {

	/**
	 * @param args[0]: the input dir paths
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		//int totalCount = 0;
		c.outBuffer = new StringBuilder();
		for(int i = 0; i < args.length; i++) {
			PriorityQueue<String> allFileCurDir = c.getAllFiles(args[i]);
			while(!allFileCurDir.isEmpty()) {
				BufferedReader br =  c.readFileByLine(args[i]+"/"+allFileCurDir.remove());
				String line = null;
				while((line = br.readLine()) != null) {
					String[] toks = getToks(line);
					for(int j = 0; j < toks.length; j++) {
						if(!toks[j].equals("")) {
							c.increaseCount(countMap, toks[j]);  // if null, exception
							//totalCount++;
						}
					}
				}
				br.close();
			}
		}
		Stack<String> res = c.getWordDecentProbOrder(countMap);
		while(!res.isEmpty()) {
			c.o(res.pop());
		}
		c.write2File(c.outBuffer.toString(), "outTrainFile");
	}
	
	public static String[] getToks(String line) {
		String ret = line.replaceAll("[^0-9\\-a-zA-Z]", " ").toLowerCase();
		return ret.split("\\s+");
	}

}
