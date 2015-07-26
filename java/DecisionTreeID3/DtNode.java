import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;


public class DtNode {
	public String featName;
	public Map<String, DtNode> children;  //map from feature value to next node
	public Map<String, Double> result;
	public int instanceNum;
	
	
	//used for trace back
	
	public DtNode parent;
	public String parentValue;  // in this case, it's only 0 or 1
	
	//public Set<String> ancestors;
	
	public DtNode() {
		children = new HashMap<String, DtNode>();
	}
	
	public DtNode(String featName) {
		this();
		this.featName = featName;
		
	}
	
	public DtNode(String featName, DtNode parent, String parentValue) {
		this(featName);
		this.parent = parent;
	}
}
