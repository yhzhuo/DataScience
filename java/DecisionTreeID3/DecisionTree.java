import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;

/**
 * 
 * @author: ZHUO, Yaohua
 * @note: this class can only be used as already built decision tree and predict instances.
 * it can not build itself. So it need other program to give it the root of a already built
 * decision tree.
 */
public class DecisionTree {
	private DtNode root;
	
	public DecisionTree(DtNode root) {
		this.root = root;
	}
	
	public Map<String, Double> classify(Set<String> binaryFeatures) {
		DtNode curNode = root;
		while(curNode.result == null) {
			if(binaryFeatures.contains(curNode.featName)) {
				curNode = curNode.children.get("1");
			} else {
				curNode = curNode.children.get("0");
			}
		}
		return curNode.result;
	}
	
	public void output(String path) throws IOException {
		PrintStream ps = new PrintStream(path);
		outputHelper(root, ps);
	}
	
	private void outputHelper(DtNode curNode, PrintStream ps) throws IOException {
		if(curNode.result == null) {
			//c.d()
			//<debug>
			if(curNode.featName != null) {
				c.d(curNode.featName);
			}
			//</debug>
			for(String val : curNode.children.keySet()) {
				outputHelper(curNode.children.get(val), ps);
			}
		} else {
			//print result and line break
			c.d("===");
			StringBuilder res = new StringBuilder();
			Stack<String> path = new Stack<String>();
			DtNode temp = curNode;
			while(temp.parent != null) {
				if(temp.parentValue.equals("0")) {
					path.push("!"+temp.parent.featName);
				} else {
					path.push(temp.parent.featName);
				}
			}
			
			boolean isFirst = true;
			while(!path.isEmpty()) {
				if(isFirst) {
					isFirst = false;
				} else {
					res.append("&");
				}
				res.append(path.pop());
			}
			//res.append(" "+root)
			res.append(" "+curNode.instanceNum+" ");
			Queue<String> order = new PriorityQueue<String>();
			for(String curCate : curNode.children.keySet()) {
				order.add(curCate+" "+curNode.children.get(curCate));
			}
			while(!order.isEmpty()) {
				res.append(order.remove()+" ");
			}
			ps.println(res.toString().trim());
		}
	}
}
