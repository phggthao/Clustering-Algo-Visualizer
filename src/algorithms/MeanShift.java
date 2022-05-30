package algorithms;

import graph.Graph;
import graph.Node;
import graphics.Animation;
import graphics.Brush;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MeanShift implements Animation {
	private Node uncategorizedNode;
	private Brush brush;
	private Timeline timeline;
	
	public MeanShift() { /* Empty constructor */ }
	public MeanShift(int bandwidth, Graph graph, Brush brush) {
		this.brush = brush;
		this.uncategorizedNode = graph.getUncategorizedNode();
		this.timeline = new Timeline();
		meanShiftClustering(graph, bandwidth);
	}

	@Override
	public void start() {
		this.timeline.play();
		System.out.println("[INFO] Start Mean Shift Clustering animation");
	}

	@Override
	public void pause() {
		this.timeline.pause();
		System.out.println("[INFO] Pause Mean Shift Clustering animation");
	}
	
	@Override
	public void resume() {
		this.timeline.play();
		System.out.println("[INFO] Resume Mean Shift Clustering animation");
	}

	@Override
	public void stop() {
		this.timeline.stop();
		this.brush.clear();
		this.brush.drawPoint(uncategorizedNode.getX(), uncategorizedNode.getY(), uncategorizedNode.getCategory());
		System.out.println("[INFO] Stop Mean Shift Clustering animation");
	}

	@Override
	public void previous() {
		this.timeline.pause();
		double previousTime = Math.floor(this.timeline.getCurrentTime().toSeconds()) - 1;
		if (previousTime < 0) {
			this.brush.clear();
			this.brush.drawPoint(uncategorizedNode.getX(), uncategorizedNode.getY(), uncategorizedNode.getCategory());
		} else {
			this.timeline.playFrom(Duration.seconds(previousTime));
			PauseTransition pause = new PauseTransition(Duration.seconds(1));
	        pause.setOnFinished((pauseEvent) -> {
	        	this.timeline.pause();
	        });
	        pause.play();
			System.out.println("[INFO] Previous step in Mean Shift Clustering animation");
		}
	}

	@Override
	public void next() {
		this.timeline.pause();
		double nextTime = Math.ceil(this.timeline.getCurrentTime().toSeconds());
		this.timeline.playFrom(Duration.seconds(nextTime));
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished((pauseEvent) -> {
        	this.timeline.pause();
        });
        pause.play();
		System.out.println("[INFO] Next step in Mean Shift Clustering animation");
	}

	/**
	 * Implementation of the Mean Shift Clustering algorithm. It is run once at the initialization of an object instance
	 * in order to build the animation required.
	 * @param graph
	 * @param bandwidth
	 */
	private void meanShiftClustering(Graph graph, int bandwidth) {
		double timeBetweenFrames = 0;
		Node currentNode = null, newNode = null;
		do {
			currentNode = graph.getUncategorizedNode();
			double shiftX = 0, shiftY = 0, scaleFactor = 0;
			for (Node originalNode : graph.getCategorizedNodes()) {
				// Calculate the distance
				double distance = distance(currentNode, originalNode);
				if (distance <= bandwidth) {
					double weight = kernel(distance, bandwidth);
					if (weight > 0) {
						// Calculate the numerator
						shiftX += originalNode.getX() * weight;
						shiftY += originalNode.getY() * weight;
						// Calculate the denominator
						scaleFactor += weight;
					}
				}
			}
			shiftX = shiftX / scaleFactor;
			shiftY = shiftY / scaleFactor;
			// Set the new shifted point
			final Node node = new Node(shiftX, shiftY, Color.BLACK);
			graph.setUncategorizedNode(node);
			// Set animation
			this.timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(timeBetweenFrames), (event) -> {
				drawStep(node, bandwidth);
				timeline.pause();
				PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
	            pause.setOnFinished((pauseEvent) -> {
	            	brush.clear();
		            drawStep(node, bandwidth);
	            	timeline.play();
	            });
	            pause.play();
			}));
			// Set new variables
			newNode = node;
			timeBetweenFrames += 1;
		} while (distance(currentNode, newNode) > 0.00005); // Run while the shifting distance is still significant
		graph.setUncategorizedNode(uncategorizedNode); // Reset uncategorized node for future runs
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
	 * Calculate the kernel using the Gaussian kernel function.
	 * @param distance
	 * @param bandwidth
	 * @return kernel
	 */
	private double kernel(double distance, int bandwidth) {
		double squareDistance = Math.pow(distance, 2);
		double squareBandwidth = Math.pow(bandwidth, 2);
		return Math.pow(Math.E, -0.5 * (squareDistance / squareBandwidth));
	}
	
	/**
	 * Draw current point with window of radius specified.
	 * @param node
	 * @param radius
	 */
	private void drawStep(Node node, int radius) {
		brush.drawPoint(node.getX(), node.getY(), Color.BLACK);
		brush.drawCircle(node.getX(), node.getY(), radius);
	}
}
