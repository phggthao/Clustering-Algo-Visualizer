package graph;

import javafx.scene.paint.Color;

public class NodeCategories {
	private static Color[] categories = {
			Color.MAGENTA,
			Color.ORANGE,
			Color.RED,
			Color.GREEN,
			Color.CYAN,
			Color.PINK
	};
	
	/**
	 * Get a category color.
	 * @return
	 */
	public static Color getColor(int index) {
		return categories[index];
	}
	
	public static int getSize() {
		return categories.length;
	}
}
