import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;


public class Files2Vectors {

	/**
	 * @param args
	 */
	private static int rare_thres;
	private static int feat_thres;
	private static String output_dir;
	private static String train_file_path;
	private static String test_file_path;
	
	public static void main(String[] args) throws IOException {
		//<debug>
		/*
		args = new String[5];
		args[0] = "wsj_sec0.word_pos";
		args[1] = "test.word_pos";
		args[2] = "3";
		args[3] = "5";
		args[4] = "out";
		*/
		//<debug>
		
		//<get input>
		train_file_path = args[0];
		test_file_path = args[1];
		rare_thres = Integer.parseInt(args[2]);
		feat_thres = Integer.parseInt(args[3]);
		output_dir = args[4];   //note: the output_dir should not end with "/"
		//</get input>
		
		c.makDirifNotExist(output_dir);
		
		//<create train_voc>
		BufferedReader trainFile = c.readFileByLine(train_file_path);
		String line = null;
		Map<String, Integer> wordCount = new HashMap<String, Integer>();
		while((line = trainFile.readLine()) != null) {
			String[] pairs = getLinePairs(line);
			for(int i = 0; i < pairs.length; i++) {
				if(!pairs[i].equals("")) {
					c.increaseCount(wordCount, pairs[i].split("/")[0]);
				}
			}
		}
		//SortedMap<Integer, List<String>> freq2Word = c.produceFreqToToken(wordCount);
		Stack<String> wordDecentFreq = c.getWordDecentFreqOrder(wordCount);
		//</create train_voc>
		//<output train_voc>
		StringBuilder train_voc_out = new StringBuilder();
		while(!wordDecentFreq.isEmpty()) {
			train_voc_out.append(wordDecentFreq.pop()+"\n");
		}
		c.write2File(train_voc_out.toString(), output_dir+"/train_voc");
		//</output train_voc>
		
		//<create init_feats>
		Map<String, Integer> featCount = new HashMap<String, Integer>();
		trainFile = null;
		line = null;
		trainFile = c.readFileByLine(train_file_path);  //may have problems here, what if it's a singleton?
		while((line = trainFile.readLine()) != null) {
			String[] pairs = getLinePairs(line);
			for(int i = 0; i < pairs.length; i++) {
				Queue<String> curTokenFeat = getFeatList(pairs, i, wordCount);
				while(!curTokenFeat.isEmpty()) {
					incNotNull(featCount, curTokenFeat.remove());
				}
			}
		}
		//</create init_feats>
		
		//<output init_feats and kept_feats>
		Stack<String> featDecentFreq = c.getWordDecentFreqOrder(featCount);  // note: the wi feature of featCount contains space at the end!!!
		StringBuilder init_feats_out = new StringBuilder();
		StringBuilder kept_feats_out = new StringBuilder();
		while(!featDecentFreq.isEmpty()) {
			String curFreq = featDecentFreq.pop();
			init_feats_out.append(curFreq.trim()+"\n");    //using trim to eliminate wi icon, init_feat must contains anything
			String[] tokenInfo = curFreq.split(" ");
			if(tokenInfo.length == 3 || Integer.parseInt(tokenInfo[1]) >= feat_thres) {   //may have problems, does the last split has the effect?
				kept_feats_out.append(curFreq.trim()+"\n"); //tokens that bigger than feat_thres or is wi feature should be added. length == 3 to determine if it's a wi feature
			}
		}
		c.write2File(init_feats_out.toString(), output_dir+"/init_feats");
		c.write2File(kept_feats_out.toString(), output_dir+"/kept_feats");
		//</output init_feats and kept_feats>
		
		//<output vectors>  note: for the name, the first line begin with 1, the first word begin with 0
		//note: since the wi feature of featCount contains space at the end, and the getFeatList return such wi feature, so don't trim it at the processing stage!!!
		outputVectors(train_file_path, wordCount, featCount, "final_train.vectors.txt");
		//c.d("test file begin");
		outputVectors(test_file_path, wordCount, featCount, "final_test.vectors.txt");
		//</output vectors>
	}
	
	private static void outputVectors(String filePath, Map<String, Integer> wordCount, Map<String, Integer> featCount, String vectorFile) throws IOException {
		String line = null;
		BufferedReader trainFile = c.readFileByLine(filePath);
		int lineCount = 1;
		while((line = trainFile.readLine()) != null) {
			StringBuilder curLineOut = new StringBuilder();
			String[] pairs = getLinePairs(line);
			for(int i = 0; i < pairs.length; i++) {
				Queue<String> curTokenFeat = getFeatList(pairs, i, wordCount);
				String[] curPair = pairs[i].split("/");
				StringBuilder curVec = new StringBuilder();
				curVec.append(lineCount+"-"+i+"-"+curPair[0]+" "); //tokName
				curVec.append(curPair[1]+" ");  //label
				while(!curTokenFeat.isEmpty()) {
					String curFeat = curTokenFeat.remove();
					if(curFeat != null && (curFeat.contains(" ") || (featCount.get(curFeat) != null && featCount.get(curFeat) >= feat_thres))) {
						curVec.append(curFeat.trim()+" 1 ");
					}
				}
				curLineOut.append(curVec.toString()+"\n");
			}
			c.write2File(curLineOut.toString(), output_dir+"/"+vectorFile);  //may have problems: can it append but not overwrite to file?
			lineCount++;
		}
	}
	
	
	//post: return the feature list, the last element of the queue is the affix array
	//note: the wi feature has a space at the begining, so remember to trim the results
	private static Queue<String> getFeatList(String[] pairs, int i, Map<String, Integer> wordCount) {
		Queue<String> ret = new LinkedList<String>();
		String[] curPair = pairs[i].split("/");   // token/tag
		//<making features>
		//<common features>
		/*
		 * prevW
		 * prev2W
		 * nextW
		 * next2W
		 * prevT
		 * prevTwoTags
		 -?: However, even we include the BOS and EOS, the first word of sentence don't have wi-2, ti-2
		 and the last word don't have wi+2. How can we handle this case? still repeatly use <s> and </s>?
		 zi: see the example
		 */
		//String prevW = "prevW=";     // write it like this to ensure it's correct
		String feat = "prevW=";
		if(i == 0) {
			feat += "BOS";
		} else {
			feat += pairs[i-1].split("/")[0];
		}
		ret.add(feat);
		feat = "prev2W=";
		if(i == 0 || i == 1) {
			feat += "BOS";
		} else {
			feat += pairs[i-2].split("/")[0];
		}
		ret.add(feat);
		feat = "nextW=";
		if(i == pairs.length-1) {
			feat += "EOS";  // alert: named by myself, not from example
		} else {
			feat += pairs[i+1].split("/")[0];
		}
		ret.add(feat);
		feat = "next2W=";
		if(i == pairs.length-1 || i == pairs.length-2) {
			feat += "EOS";
		} else {
			feat += pairs[i+2].split("/")[0];
		}
		ret.add(feat);
		feat = "prevT=";
		//String prevT = feat;
		if(i == 0) {
			feat += "BOS";
		} else {
			feat += pairs[i-1].split("/")[1];
		}
		ret.add(feat);
		String prevT = feat;
		feat = "prevTwoTags=";
		if(i == 0 || i == 1) {
			feat += "BOS";
		} else {
			feat += pairs[i-2].split("/")[1];
		}
		feat += "+"+prevT.split("=")[1];        // use already exist variable
		ret.add(feat);
		//</common features>
		//String curW = null;
		
		// first 4 is pref 1-4 last 4 is suf 1-4
		//String[] preAndSuf = {null, null, null, null, null, null, null, null};
		//String containNum = null;
		//String containUC = null;
		//String containHy = null; // alert: named by myself, not from example
		//<features for rare words>
		
		if(wordCount.get(curPair[0]) == null || wordCount.get(curPair[0]) < rare_thres) {  //rare word
			processAff(ret, curPair[0]);
			if(curPair[0].replaceAll("[^0-9]", "").length() != 0) {
				ret.add("containNum");
			}
			if(curPair[0].replaceAll("[^A-Z]", "").length() != 0) {
				ret.add("containUC");
			}
			if(curPair[0].contains("-")) {
				ret.add("containHy");
			}
		} else {
			ret.add(" curW="+pairs[i].split("/")[0]); //use a space as a mark of wi feature which MUST BE KEPT
		}
		//</making features>
		return ret;
	}
	
	private static void incNotNull(Map<String, Integer> countMap, String element) {
		if(countMap != null && element != null) {
			c.increaseCount(countMap, element);
		}
	}
	
	/*
	 * ?: There is no some commands such as info2vectors
	 */
	
	//pre: preAndSuf is a String array contains 8 null elements
	//post: put the affix of token to the preAndSuf, keep it null if token does't have such affix
	/*
	 * This example shows pref and suf can collide:
	 * 3-11-Gold NNP prev2W=of 1 prevT=NNP 1 prevTwoTags=IN+NNP 1 containUC 1 pref=G 1 pref=Go 1 suf=d 1 suf=ld 1 suf=old 1
	 * ?: But what if the length of token < 4? So it would lose some pref or suf in init_feat?
	 */
	private static void processAff(Queue<String> ret, String token) {
		for(int i=0; i < token.length() && i < 4; i++) {   // may have problems here
			ret.add("pref="+token.substring(0,i+1));
			ret.add("suf="+token.substring(token.length()-1-i));
		}
	}
	
	private static String[] getLinePairs(String line) {
		line = line.replace(",", "comma").trim();
		return line.split("\\s+");
	}
}
