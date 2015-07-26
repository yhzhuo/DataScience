import java.util.LinkedList;
import java.util.Queue;


public class BNode {
	public double sumProb;
	public BNode parent;
	public String tag;
	public int level;
	public double logProb;
	public Queue<BNode> children;
	
	public BNode(String tag, int level) {
		children = new LinkedList<BNode>();
		this.tag = tag;
		this.level = level;
		logProb = 0;
	}
	
	public BNode(String tag, int level, double prob) {
		this(tag, level);
		this.logProb = c.log(prob, Math.E);  //is the E right?
	}
}
