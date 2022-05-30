package graphics;

import java.util.ArrayList;

import graph.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brush {
	private GraphicsContext graphicsContext;
	private double canvasWidth, canvasHeight;
	
	public Brush(GraphicsContext graphicsContext, double canvasWidth, double canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.graphicsContext = graphicsContext;
		graphicsContext.setLineWidth(1);
	}
	
	/**
	 * Draw the given nodes list.
	 * @param nodes
	 */
	public void drawGraph(ArrayList<Node> nodes) {
		for (Node node : nodes) {
			drawPoint(node.getX(), node.getY(), node.getCategory());
		}
	}
	
	/**
	 * Draw the given nodes list of centers.
	 * @param nodes
	 */
	public void drawGraphCenters(ArrayList<Node> nodes) {
		for (Node node : nodes) {
			drawCenter(node.getX(), node.getY(), node.getCategory());
		}
	}
	
	/**
	 * Draw a non-categorized point on the canvas at (x, y)
	 * with width = height = 5 and arc width = arc height = 1.
	 * @param x
	 * @param y
	 */
	public void drawPoint(double x, double y, Color color) {
		graphicsContext.setFill(color);
		graphicsContext.fillRect(x, y, 5, 5);
	}
	
	/**
	 * Draw a node lists of centers
	 * with width = height = 10 and arc width = arc height = 1.
	 * @param x
	 * @param y
	 */	
	public void drawCenter(double x, double y, Color color) {
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.fillRect(x-1, y-1, 12, 12);
		graphicsContext.setFill(color);
		graphicsContext.fillRect(x, y, 10, 10);
	}
	
	/**
	 * Draw a circle with the specified center coordinates.
	 * @param x
	 * @param y
	 * @param radius
	 */
	public void drawCircle(double x, double y, int radius) {
		graphicsContext.setStroke(Color.BLUE);
		graphicsContext.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
	}
	
	public void drawLine(double x1, double y1, double x2, double y2) {
		graphicsContext.setStroke(Color.BLUE);
		graphicsContext.strokeLine(x1, y1, x2, y2);
	}
	
	/**
	 * Clear the canvas
	 */
	public void clear() {
		graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
	}
	
	/**
	 * Clear a point
	 */
	public void clearPoint(double x, double y) {
		graphicsContext.clearRect(x, y, 5, 5);
	}
	
	public void clearCenter(double x, double y) {
		graphicsContext.clearRect(x-1, y-1, 12, 12);
	}
}
