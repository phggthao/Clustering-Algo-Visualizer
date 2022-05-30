package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import graph.Graph;
import graph.Node;
import graphics.Animation;
import graphics.Brush;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class KNN implements Animation {
	private Node uncategorizedNode;
	private Brush brush;
	private Timeline timeline;
	
	public KNN() {/* Empty constructor*/ }
	
	public KNN(int k, Graph graph, Brush brush) {
		this.brush = brush;
		this.uncategorizedNode = graph.getUncategorizedNode();
		this.timeline = new Timeline();
		KNNClustering(graph, uncategorizedNode, k);
	}

	@Override
	public void start() {
		this.timeline.play();
		System.out.println("[INFO] Start K-Nearest Neighbour animation");
	}

	@Override
	public void pause() {
		this.timeline.pause();;
		System.out.println("[INFO] Pause K-Nearest Neighbour animation");
	}
	
	@Override
	public void resume() {
		this.timeline.play();
		System.out.println("[INFO] Resume K-Nearest Neighbour animation");
	}

	@Override
	public void stop() {
		this.timeline.stop();
		this.brush.clear();
		this.brush.drawPoint(uncategorizedNode.getX(), uncategorizedNode.getY(), uncategorizedNode.getCategory());
		System.out.println("[INFO] Stop K-Nearest Neighbour animation");
	}

	@Override
	public void previous() {
		this.timeline.pause();
		double previousTime = Math.floor(this.timeline.getCurrentTime().toSeconds()) - 1;
		if (previousTime < 0) {
			this.brush.clear();
			this.brush.drawPoint(uncategorizedNode.getX(), uncategorizedNode.getY(), Color.BLACK);
		} 
		else {
			this.timeline.playFrom(Duration.seconds(previousTime));
			PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
	        pause.setOnFinished((pauseEvent) -> {
	        	this.timeline.pause();
	        });
	        pause.play();
			System.out.println("[INFO] Previous step in K-Nearest Neighbour animation");
		}
	}

	@Override
	public void next() {
		this.timeline.pause();
		double nextTime = Math.ceil(this.timeline.getCurrentTime().toSeconds());
		this.timeline.playFrom(Duration.seconds(nextTime));
		PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished((pauseEvent) -> {
        	this.timeline.pause();
        });
        pause.play();
		System.out.println("[INFO] Next step in K-Nearest Neighbour animation");
	}

	private void KNNClustering(Graph graph, Node newNode, int k) {
		double timeBetweenFrames = 0;
		ArrayList<Node> categorizedNode = graph.getCategorizedNodes();
		ArrayList<Node> nearestNeighbors = new ArrayList<Node>();
		Map<Double, Node> nodeDistance = new HashMap<Double, Node>();
		
		//Get the distance from the uncategorized node to all categorized nodes
		this.timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(timeBetweenFrames), (event) -> {
			brush.clear();
		}));
		for (Node i: categorizedNode) {
			nodeDistance.put(distance(newNode,i), i); // store Node with its distance
			//Set animation: Calculate distance
			this.timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(timeBetweenFrames), (event) -> {
				brush.drawLine(newNode.getX(), newNode.getY(), i.getX(), i.getY());
				System.out.println("[INFO] Get distance");
			}));
		}
		
		//Sort the distances in ascending order
		Map<Double, Node> sortedMap = nodeDistance
				.entrySet()
				.stream()
				.sorted(Map.Entry.<Double, Node>comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
		
		//Get the list of nearest neighbors
		for (double j: sortedMap.keySet()) {
			if (nearestNeighbors.size() < k) nearestNeighbors.add(sortedMap.get(j));
			else break;
		}
		//Set animation: Get the list of KNN
		this.timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(timeBetweenFrames+1), (event) -> {
			brush.clear();
			brush.drawPoint(newNode.getX(), newNode.getY(), Color.BLACK);
			brush.drawCircle(newNode.getX(), newNode.getY(), (int) distance(newNode, nearestNeighbors.get(k-1)));
			System.out.println("[INFO] Show the list of KNN");
		}));
		
		
		//Get the category that appears the most within KNN, set color for uncategorized node
		newNode.setCategory(maxCategory(nearestNeighbors));
		//Set animation: Change the color of uncategorized node
		this.timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(timeBetweenFrames+2), (event) -> {
			brush.clear();
			brush.drawPoint(newNode.getX(), newNode.getY(), newNode.getCategory());
			if (graph.add(newNode)) System.out.println("[INFO] KNN complete");
		}));
	}
	
	/**
	 * Calculate the Euclidean distance between two nodes.
	 * @param source
	 * @param destination
	 * @return distance
	 */
	private double distance(Node source, Node destination) {
		double squareX = Math.pow(destination.getX() - source.getX(), 2);	// Calculate x^2
		double squareY = Math.pow(destination.getY() - source.getY(), 2);	// Calculate y^2
		return Math.sqrt(squareX + squareY);
	}
	
	/**
	 * Get the category that appears most time
	 * @param nodeList
	 * @return
	 */
	private Color maxCategory(ArrayList<Node> nodeList) {
		Color[] categories = {Color.MAGENTA, Color.ORANGE,Color.RED,Color.GREEN,Color.CYAN,Color.PINK};
		Color category;
		int countMagenta = 0, countOrange = 0, countRed = 0, countGreen = 0, countCyan = 0, countPink = 0;
		int[] countColor = {countMagenta, countOrange, countRed, countGreen, countCyan, countPink};
		Map<Integer, Color> categoryCount = new HashMap<Integer, Color>();
		
		for (Node i: nodeList) {
			category = i.getCategory();
			for (int j=0;j<categories.length;j++) {
				if (category.equals(categories[j])) {
					countColor[j]++; break;
				}
			}
		}
		for (int i=0;i<categories.length;i++) {
			categoryCount.put(countColor[i], categories[i]);
		}
		Map<Integer, Color> sortedCategory = categoryCount
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.<Integer, Color>comparingByKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
		Arrays.sort(countColor);

		return sortedCategory.get(countColor[countColor.length-1]);
	}
}
