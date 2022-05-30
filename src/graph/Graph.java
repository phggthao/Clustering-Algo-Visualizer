package graph;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;

public class Graph {
	private ArrayList<Node> categorizedNodes;
	private Node uncategorizedNode;
	int numClusters = 0;

	public int getNumClusters() {
		return numClusters;
	}

	// Constructor
	public Graph() {
		this.categorizedNodes  = new ArrayList<>();
	}
	
	/**
	 * Get a list of all nodes in the graph.
	 * @return nodes
	 */
	public ArrayList<Node> getNodes() {
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.addAll(this.categorizedNodes);
		if (uncategorizedNode != null)
			nodes.add(uncategorizedNode);
		return nodes;
	}
	
	/**
	 * Get a list of all categorized nodes in the graph.
	 * @return nodes
	 */
	public ArrayList<Node> getCategorizedNodes() {
		return this.categorizedNodes;
	}
	
	/**
	 * Get the uncategorized node in the graph.
	 * @return nodes
	 */
	public Node getUncategorizedNode() throws NullPointerException {
		return this.uncategorizedNode;
	}
	
	/**
	 * Set the uncategorized node.
	 * @param node
	 * @return true
	 */
	public boolean setUncategorizedNode(Node node) {
		this.uncategorizedNode = node;
		return true;
	}

	/**
	 * Add a new node to the current graph.
	 * @param node
	 * @return true - if a new node is added.
	 */
	public boolean add(Node node) {
		if (node.getCategory() == Color.BLACK) {
			this.uncategorizedNode = node;
			return true;
		}
		return this.categorizedNodes.add(node);
	}
	
	/**
	 * Clear the current graph.
	 * @return true
	 */
	public boolean clear() {
		this.categorizedNodes.clear();
		this.uncategorizedNode = null;
		return true;
	}

	/**
	 * Generate a random graph with a random number of clusters and the number of nodes specified by the user.
	 * The name of each node is its id in generation order.
	 * 
	 * The number of clusters is first randomly generated. At each remaining node, a random cluster center is
	 * selected and used as reference for the node position. An offset value for each axis is randomly generated
	 * and added to the cluster center to calculate the position of the new node.
	 * 
	 * @param numNodes - number of nodes to generate
	 * @param maxX - max X-value of canvas
	 * @param maxY - max Y-value of canvas
	 * @return true
	 */
	public boolean generate(int numNodes, int maxX, int maxY) {
		// Initiate generator
		Random rand = new Random();
		// Generate number of clusters
		while (numClusters < 3)
			numClusters = rand.nextInt(NodeCategories.getSize());
		// Generate cluster centers
		ArrayList<Node> centers = new ArrayList<>();
		for (int i = 0; i < numClusters; i++) {
			double x = rand.nextDouble() * maxX;	// Generate x-coordinate
			double y = rand.nextDouble() * maxY;	// Generate y-coordinate
			Node node = new Node(x, y, NodeCategories.getColor(i));	// Create center node
			centers.add(node);	// Add center node to list of centers
			add(node);			// Add center node to list of nodes
		}
		// Generate the rest of the nodes
		int nodeCounter = 0;
		while (nodeCounter < numNodes - numClusters) {
			Node center = centers.get(rand.nextInt(numClusters));	// Randomly choose a center
			// Generate offsets
			double offsetX, offsetY;
			if (numNodes >= 500) {
				offsetX = rand.nextDouble() * rand.nextInt(numNodes / numClusters);	// Generate offset in the x-direction
				offsetY = rand.nextDouble() * rand.nextInt(numNodes / numClusters);	// Generate offset in the y-direction
			} else {
				offsetX = rand.nextInt(numNodes / numClusters);	// Generate offset in the x-direction
				offsetY = rand.nextInt(numNodes / numClusters);	// Generate offset in the y-direction
			}
			// If a random floating point number is less than 0.5 then negate the offset
			if (rand.nextDouble() < 0.5)
				offsetX *= -1;
			if (rand.nextDouble() < 0.5)
				offsetY *= -1;
			// Calculate new node position
			double x = center.getX() + offsetX;
			double y = center.getY() + offsetY;
			// If the offset is out of the screen then re-generate the node
			if (x < 0 || y < 0 || x > maxX || y > maxY)
				continue;
			// else, add the new node to graph
			add(new Node(x, y, center.getCategory()));
			// Increment number of nodes generated
			nodeCounter++;
		}
		return true;
	}
}
