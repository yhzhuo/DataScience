
import java.util.*;

public class Graph<N, E> {
	
	/*
	 * Abstraction function:
	 * this.Graph is the Map that implements the graph
	 * the inner class Graph.Edge is the the class implements the edges in this Graph. 
	 * 
	 * Representative Invariant:
	 * 1: no duplicate keys in this.Graph, which means no same nodes 
	 * in the graph  //this is already guaranteed by Set structure, so don't need to test
	 * 2: Graph != null
	  	 * 3: no same keys in every node's children map
		 * 4: no any keys in every node's children maps maps to null or a empty set
	 */
	
	private Map<N, Node<N, E>> Graph;
	
	//this is the flag that taught in class to appropriate remove checkRep when the program is OK.
	private final boolean needCheckRep = false;
	
	//This class implements the nodes of the Graph
	private final class Node<N, E> {
		
		
		/*
		 * Abstraction function:
		 * 1: this.children is a map that maps all the edges of this node to other
		 * nodes (including itself) the key is the data of a Node that represents a child node
		 * of this node, and the value is the set of edges that maps from this node
		 * to the child node represented by the key
		 * 
		 * 
		 * Representative Invariant:
		 * 1: this.children != null
		 * 
		 * Note: This node is not intended to be used by other class, because it's not generic, which means
		 * that it can only fit in the requirement of this String based graph. Besides that, the node is a
		 * very important functional part of the Graph and the Graph cannot work with other objects, So making 
		 * it as a out class may make the Graph suffer some unnecessary dangerous (such as the file Node.java 
		 * is missing) So, after two debates, I and another TA decided to make it as a inner class.
		 */
		
		
		//the edges of this node
		public final Map<N, Set<E>> children;
		

		/**
		 * This is the constructor of a node in the Graph
		 * 
		 * @param data: the data that stores in this node
		 * @effects: The node is constructed with the given 
		 * data without any edges
		 */
		public Node() {
			children = new HashMap<N, Set<E>>();
			checkRep();
		}
		
		/* 
		 * Representative Invariant:
		 * 1: this.parents != null
		 * 2: this.children != null
		 */
		private void checkRep() {
			if(!needCheckRep) {
				return;
			}
			//this.parents != null && this.children != null
			if(this.children == null) {
				throw new RuntimeException("the Representative Invariant of node is failed");
			}
		}
	}
	
	
	/**
	 * This is the constructor of Graph object
	 * @effects: Construct a empty Graph object without nodes
	 */
	public Graph() {
		Graph = new HashMap<N, Node<N, E>>();   //TODO: change
		checkRep();
	}
	
	/**
	 * put a data into the graph
	 * 
	 * @param data: the data that need to be put in to the graph
	 * @return: false if the nodes that has this data is already in the Graph or data == null.
	 * This means the node is not added. True if the addition is successful.
	 */
	public boolean addNode(N data) {
		if(data == null || Graph.keySet().contains(data)) {
			return false;
		}
		Graph.put(data, new Node<N, E>());
		checkRep();
		return true;
	}
	
	/**
	 * delete a node from the graph
	 * 
	 * @param nodeName: name of the node that need to be removed
	 * @return true if the node is in the graph and is removed
	 * return false otherwise
	 */
	/*
	public boolean deleteNode(String nodeName) {
		Set<String> nodes = Graph.keySet();
		if(nodes.contains(nodeName)) {
			Node rmNode = Graph.get(nodeName);
			//unlink parents
			Iterator<String> i = rmNode.parents.iterator();
			while(i.hasNext()) {
				String cur = i.next();
				Node curNode = Graph.get(cur);
				curNode.children.remove(nodeName);
				i.remove();
			}
			//unlink children
			i = rmNode.children.keySet().iterator();
			while(i.hasNext()) {
				String cur = i.next();
				Node curNode = Graph.get(cur);
				curNode.parents.remove(nodeName);
				i.remove();
			}
			Graph.remove(nodeName);
			return true;
		}
		return false;
	}
	*/
	/**
	 * delete all edges between two nodes
	 * 
	 * @param src: the first node
	 * @param dest: the second node
	 * @return false if dest does not link with src.
	 * otherwise all edges between them are deleted
	 * @requires: src is in the graph otherwise the
	 * behavior is undefined.
	 *  
	 */
	/*
	private boolean unLink(String src, String dest) {
		Node srcNode = Graph.get(src);
		Node destNode = Graph.get(dest);
		if(destNode != null) {
			if(srcNode.parents.contains(dest)) {
				destNode.children.remove(src);
				srcNode.parents.remove(dest);
				checkRep();
				return true;
			} 
			if(srcNode.children.keySet().contains(dest)) {
				destNode.parents.remove(src);
				srcNode.children.remove(dest);
				checkRep();
				return true;
			}
		}
		checkRep();
		return false;
	}
	*/
	
	/**
	 * Construct a edge link from the parent node to child node
	 * 
	 * @modifies: parent node to add a new edge
	 * @param parentData: the data in parent node
	 * @param childData: the data in child node
	 * @param label: the label of the edge
	 * @return: false if parentData is null || graph does not contain parent || graph does not contain child || childData is null 
	 * || parent has edges with same labels link to child || label is null
	 */
	public boolean addEdge(N parentData, N childData, E label) {
		if(checkGraphHasOperands(parentData, childData) && label != null) {
			boolean result = addEdgeHelper(parentData, childData, label);
			checkRep();
			return result;
		}
		checkRep();
		return false;
	}
	
	
	/**
	 * Add a edge with a label and link the edge to a child
	 * 
	 * @param child: the data of the child that the edge link to 
	 * @param label: the label of the edge
	 * @return: false if the child has already linked by
	 * a edge with same label from this Node
	 * @requires: all parameters are not null
	 */
	private boolean addEdgeHelper(N parent, N child, E label) {
		Node<N, E> parentNode = Graph.get(parent);
		if(parentNode.children.keySet().contains(child)) {
			if(parentNode.children.get(child).contains(label)) {
				return false;
			}
		} else {
			parentNode.children.put(child, new HashSet<E>());
		}
		parentNode.children.get(child).add(label);
		return true;
	}

	/**
	 * get the set of all direct children of parent node
	 * 
	 * @param parentData: the data that contained in parent node that we need to find all children of it
	 * @return: a Map Set that contains all children of parent map to the labels connect 
	 * them from the parent. return null if no such parent exist, or parentData is null 
	 * @effects: the child nodes in the set is sorted in alphabetical order
	 * with respect to their data, and the edges in each nodes is sorted in alphabetical
	 * with respect to their label. Clients cannot modify the returned entry set
	 */
	public Map<N, Set<E>> getAllChildren(N parentData) {
		if(parentData != null) {
			Node<N, E> parent = Graph.get(parentData);
			if(parent != null) {
				return Collections.unmodifiableMap(parent.children);
			}
		}
		return null;
	}
	
	/**
	 * Delete a edge link from the parent node to child node
	 * 
	 * @modifies: parent node to delete a edge
	 * @param parentData: the data in parent node
	 * @param childData: the data in child node
	 * @param label: the label of the edge that need to be deleted
	 * @return: false if the parent node is not connected with child node by the edge with input label otherwise true
	 * || parentData is null || graph does not contain parent || graph does not contain child || childData is null 
	 *  || label is null
	 */
	
	public boolean deleteEdge(N parentData, N childData, E label) {
		if(checkGraphHasOperands(parentData, childData) && label != null) {
			boolean result = deleteEdgeHelper(parentData, childData, label);
			checkRep();
			return result;
		}
		return false;
	}
	
	/**
	 * Delete a edge with a label and link this to a child
	 * 
	 * @param child: the data of the child that the edge link to 
	 * @param label: the label of the edge
	 * @return: false if there is no edge with such label in this node
	 * that connect to the child, and no edges deleted
	 * @requires: all inputs are not null. parent and child map to
	 * certain nodes in the graph
	 */
	
	public boolean deleteEdgeHelper(N parent, N child, E label) {
		Node<N, E> parentNode = Graph.get(parent);
		if(parentNode.children.keySet().contains(child)) {
			Set<E> labels = parentNode.children.get(child);
			if(labels.contains(label)) {
				labels.remove(label);
				if(labels.isEmpty()) {
					parentNode.children.remove(child);
				}
				checkRep();
				return true;
			}
		}
		checkRep();
		return false;
	}
	
	
	/**
	 * check if the input operands are valid in this graph
	 * 
	 * @param operand1: first operand need to be checked
	 * @param operand2: second operand need to be checked
	 */
	private boolean checkGraphHasOperands(N operand1, N operand2) {
		Set<N> valuesInGraph = Graph.keySet();
		if(operand1 == null || operand2 == null || !valuesInGraph.contains(operand1) || !valuesInGraph.contains(operand2)) {
			return false;
		}
		return true;
	}
	
	/**
	 * get all nodes in the set
	 * 
	 * @return: all data of nodes of this Graph
	 * @effects: the values of nodes is sorted in alphabetical order with respect to their data.
	 * Clients cannot modify the returned nodes
	 */
	public Set<N> getAllNodes() {
		return Collections.unmodifiableSet(Graph.keySet());
	}
	
	/**
	 * clear the graph
	 * 
	 * @modifies: remove all the keys and values in it.
	 */
	
	public void clear() {
		Graph.clear();
	}
	
	
	
	/* Representative Invariant:
	 * 1: no duplicate keys in this.Graph, which means no same nodes 
	 * in the graph  ok
	 * 2: Graph != null    ok
	  	 * 3: no same keys in every node's children map
		 * 4: no any keys in every node's children maps maps to null or a empty set
	 */
	private void checkRep() {
		if(!needCheckRep) {
			return;
		}
		//Graph != null
		if(Graph == null) {
			throw new RuntimeException("the Representative Invariant of Graph is failed");
		}
		//no duplicate keys in this.Graph
		for(N cur : Graph.keySet()) {
			for(N temp : Graph.keySet()) {
				if(cur != temp && cur.equals(temp)) {
					throw new RuntimeException("the Representative Invariant of Graph is failed, see duplicate keys");
				}
			}
			//3
			Node<N, E> curNode = Graph.get(cur);
			for(N cur3 : curNode.children.keySet()) {
				for(N temp : curNode.children.keySet()) {
					if(cur3 != temp && cur3.equals(temp)) {
						throw new RuntimeException("the Representative Invariant of Graph is failed, see duplicate keys");
					}
				}
				//4
				if(curNode.children.get(cur3) == null || curNode.children.get(cur3).isEmpty()) {
					throw new RuntimeException("the Representative Invariant of Graph is failed");
				}
				
			}
		}
	}
}