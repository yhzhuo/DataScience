import java.io.IOException;
import java.util.Scanner;


public class conv_format {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//<debug>
		//Scanner input = new Scanner(c.readFile(System.in));
		//</debug>
		
		//<get real input>
		Scanner input = new Scanner(System.in);
		//</get real input>
		while(input.hasNextLine()) {
			String[] twoParts = input.nextLine().split("=>");
			twoParts[0] = twoParts[0].trim();
			twoParts[1] = twoParts[1].trim();
			String[] words = twoParts[0].split(" ");
			String[] tags = twoParts[1].split(" ");
			StringBuilder curOut = new StringBuilder();
			for(int i = 0; i < words.length; i++) {
				curOut.append(words[i]+"/"+tags[i+1]+" ");
			}
			c.o(curOut.toString().trim());
		}
		input.close();
	}
}
