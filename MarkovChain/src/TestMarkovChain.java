import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class TestMarkovChain {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		/*
		String[][] trainData = {
			{"aaa", "ddd"},
			{"ccc", "bbb"}
		};
		
		String[][] testData = {
			{"aaa", "bbb"},
			{"ccc", "ddd"}
		};
		
		List<String[]> trainingData = convertToVectorSet(trainData);
		List<String[]> testingData = convertToVectorSet(testData);
		MarkovChain classifier1 = new MarkovChain(1);
		classifier1.train(trainingData);
		classifier1.test(testingData);
		*/
		BufferedReader br = c.readFileByLine("data.txt");
		String line = null;
		String[][] data = new String[300][];
		int count = 0;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			String tag = MarkovChain.preprocessTag(line.substring(line.lastIndexOf(" ")+1, line.length()));
			String name = MarkovChain.preprocessName(line.substring(0, line.lastIndexOf(" ")));
			String[] curVector = {tag, name};
			data[count] = curVector;
			count++;
		}
		shuffle(data);
		List<String[]> training = convertToVectorSet(data, 0, 250);
		List<String[]> testing = convertToVectorSet(data, 250, data.length);
		
		/*
		 * yaohua zhuo y
Sina Yeganeh y
		 */
		String[] sina = {MarkovChain.preprocessName("Sina Yeganeh"), MarkovChain.preprocessTag("n")};
		String[] yaohua = {MarkovChain.preprocessName("yaohua zhuo"), MarkovChain.preprocessTag("y")};
		testing.add(sina); testing.add(yaohua);
		MarkovChain classifier1 = new MarkovChain(4);
		classifier1.train(training);
		classifier1.test(testing);
		
	}
	
	private static void shuffle(String[][] featVectors) {
		for (int i = 0; i < featVectors.length; i++) {
			int j = (int) Math.floor(Math.random()*(featVectors.length-1-i)) + i;
			String[] tmp = featVectors[j];
			featVectors[j] = featVectors[i];
			featVectors[i] = tmp;
		}
	}
	
	/**
	 * 
	 * @param data: the data to be convert
	 * @param start: first index we want
	 * @param end: first index we don't want
	 * @return a List of string array represents feature vectors
	 */
	private static List<String[]> convertToVectorSet(String[][] data, int start, int end) {
		List<String[]> ret = new LinkedList<String[]>();
		for (int i = start; i < data.length && i < end; i++) {
			ret.add(data[i]);
		}
		return ret;
	}

}
