package graph;

import javafx.scene.paint.Color;

public class Node {
	private double x, y;
	private String name = "";
	private Color category = Color.BLACK;

	public Node(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Node(double x, double y, Color category) {
		this.x = x;
		this.y = y;
		this.category = category;
	}
	public Node(double x, double y, String name) {
		this.x = x;
		this.y = y;
		this.name = name;
	}
	public Node(double x, double y, String name, Color color) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.category = color;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setCategory(Color category) {
		this.category = category;
	}
	
	public String getName(String name) {
		return this.name;
	}
	
	public double getX() {
		return this.x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return this.y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public Color getCategory() {
		return this.category;
	}
}