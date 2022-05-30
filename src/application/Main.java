package application;

import algorithms.KMeans;
import algorithms.KNN;
import algorithms.MeanShift;
import graph.Graph;
import graph.Node;
import graphics.Animation;
import graphics.Brush;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	private static Graph graph = new Graph();
	private static Scene scene = null;
	private Animation currentAnimation = null;

	@Override
	public void start(Stage primaryStage) {
		try {
			TabPane root = (TabPane)FXMLLoader.load(getClass().getResource("Sample.fxml"));
			scene = new Scene(root, 1280, 720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("Algorithm Simulator (Group 7 - Topic 1)");
			primaryStage.setResizable(false);
			primaryStage.show();
			
			// Get all canvases
			Canvas canvasGraph = (Canvas) scene.lookup("#canvasGraph");
			Canvas canvasKNN = (Canvas) scene.lookup("#canvasKNN");
			Canvas canvasKMeans = (Canvas) scene.lookup("#canvasKMeans");
			Canvas canvasMeanShift = (Canvas) scene.lookup("#canvasMeanShift");
			
			// Disable algorithm canvases
			canvasKNN.setVisible(false);
			canvasKMeans.setVisible(false);
			canvasMeanShift.setVisible(false);
			
			final Brush brushGraph = new Brush(canvasGraph.getGraphicsContext2D(), canvasGraph.getWidth(), canvasGraph.getHeight());
			
			// Implementation of Generate Graph button function
			Button btnGenerateGraph = (Button) scene.lookup("#btnGenerateGraph");
			btnGenerateGraph.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					// Dialog config
					TextInputDialog getNumberNodes = new TextInputDialog();
					getNumberNodes.setTitle("Get Number of Nodes Dialog");
					getNumberNodes.setHeaderText("Enter number of nodes (must be an integer between 200 and 1000 inclusive)");
					getNumberNodes.show();
					// Input check: disable OK button if input is invalid
					Button okButton = (Button) getNumberNodes.getDialogPane().lookupButton(ButtonType.OK);
					TextField inputField = getNumberNodes.getEditor();
					BooleanBinding isValid = Bindings.createBooleanBinding(() -> !isValid(inputField.getText()), inputField.textProperty());
					okButton.disableProperty().bind(isValid);
					// Set action on OK button click - change the current animation to animate Mean Shift algorithm
					okButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							// Clear the previous graph and canvas
							graph.clear();
							brushGraph.clear();
							canvasKMeans.setVisible(false);
							canvasKNN.setVisible(false);
							canvasMeanShift.setVisible(false);
							// Reset the animation
							currentAnimation = null;
							// Generate new graph and draw it
							graph.generate(Integer.parseInt(inputField.getText()), 1000, 650);
							brushGraph.drawGraph(graph.getNodes());
						}
					});
				}
				/**
				 * Check whether the dialog input is an integer or not.
				 * @param text
				 * @return true - if text is integer
				 */
				private boolean isValid(String text) {
					int number = 0;
					try {
						number = Integer.parseInt(text);
					} catch (Exception e) {
						return false;
					}
					return (number >= 200 && number <= 1000);
				}
			});
			
			// Implementation of KNN button function
			Button btnKNN = (Button) scene.lookup("#btnKNN");
			btnKNN.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					// Dialog config
					TextInputDialog getKDialog = new TextInputDialog();
					getKDialog.setTitle("Get K-value Dialog");
					getKDialog.setHeaderText("Enter K-value (must be an integer)");
					getKDialog.show();
					// Input check: disable OK button if input is invalid
					Button okButton = (Button) getKDialog.getDialogPane().lookupButton(ButtonType.OK);
					TextField inputField = getKDialog.getEditor();
					BooleanBinding isValid = Bindings.createBooleanBinding(() -> !isValid(inputField.getText()), inputField.textProperty());
					okButton.disableProperty().bind(isValid);
					// Set action on OK button click - change the current animation to animate KNN algorithm
					okButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							// Disable other algorithm canvases
							canvasKMeans.setVisible(false);
							canvasMeanShift.setVisible(false);
							// Enable current algorithm canvas
							canvasKNN.setVisible(true);
							// Create new brush for current algorithm canvas
							Brush brushKNN = new Brush(canvasKNN.getGraphicsContext2D(), canvasKNN.getWidth(), canvasKNN.getHeight());
							brushKNN.clear();
							canvasKNN.setOnMouseClicked((event) -> {
								allowClick(brushKNN, event);
								currentAnimation = new KNN(Integer.parseInt(inputField.getText()), graph, brushKNN); // Create new animation for Mean Shift clustering
							});
						}
					});
				}
			});
			// Implementation of K-Means button function
			Button btnKMeans = (Button) scene.lookup("#btnKMeans");
			btnKMeans.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					// Dialog config
					TextInputDialog getKDialog = new TextInputDialog();
					getKDialog.setTitle("Get K-value Dialog");
					getKDialog.setHeaderText("Enter K-value (must be an integer between 1 and 6)");
					getKDialog.show();
					// Input check: disable OK button if input is invalid
					Button okButton = (Button) getKDialog.getDialogPane().lookupButton(ButtonType.OK);
					TextField inputField = getKDialog.getEditor();
					BooleanBinding isValid = Bindings.createBooleanBinding(() -> !isValid(inputField.getText()), inputField.textProperty());
					okButton.disableProperty().bind(isValid);
					// Set action on OK button click - change the current animation to animate KMeans algorithm
					okButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							// Disable other algorithm canvases
							canvasKNN.setVisible(false);
							canvasMeanShift.setVisible(false);
							// Enable current algorithm canvas
							canvasKMeans.setVisible(true);
							// Create new brush for current algorithm canvas
							Brush brushKMeans = new Brush(canvasKMeans.getGraphicsContext2D(), canvasKMeans.getWidth(), canvasKMeans.getHeight());
							brushKMeans.clear();
							try {
								Node uncategorizedNode = graph.getUncategorizedNode();
								brushGraph.clearPoint(uncategorizedNode.getX(), uncategorizedNode.getY());							// Clear graph canvas' uncategorized node if exists
								currentAnimation = new KMeans(Integer.parseInt(inputField.getText()), graph, brushKMeans);			// Create new animation for K-Means Clustering
							}
							catch (NullPointerException npe) {
								currentAnimation = new KMeans(Integer.parseInt(inputField.getText()), graph, brushKMeans);			// Create new animation for K-Means Clustering
							}
						}
					});
				}
				private boolean isValid(String text) {
					int number = 0;
					try {
						number = Integer.parseInt(text);
					} catch (Exception e) {
						return false;
					}
					return (number >= 1 && number <= 6);
				}
			});
			// Implementation of MeanShift button function
			Button btnMeanShift = (Button) scene.lookup("#btnMeanShift");
			btnMeanShift.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					// Dialog config
					TextInputDialog getBandwidthDialog = new TextInputDialog();
					getBandwidthDialog.setTitle("Get Bandwidth Dialog");
					getBandwidthDialog.setHeaderText("Enter bandwidth value (must be an integer)");
					getBandwidthDialog.show();
					// Input check: disable OK button if input is invalid
					Button okButton = (Button) getBandwidthDialog.getDialogPane().lookupButton(ButtonType.OK);
					TextField inputField = getBandwidthDialog.getEditor();
					BooleanBinding isValid = Bindings.createBooleanBinding(() -> !isValid(inputField.getText()), inputField.textProperty());
					okButton.disableProperty().bind(isValid);
					// Set action on OK button click - change the current animation to animate Mean Shift algorithm
					okButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							// Disable other algorithm canvases
							canvasKNN.setVisible(false);
							canvasKMeans.setVisible(false);
							// Enable current algorithm canvas
							canvasMeanShift.setVisible(true);
							// Create new brush for current algorithm canvas
							Brush brushMeanShift = new Brush(canvasMeanShift.getGraphicsContext2D(), canvasMeanShift.getWidth(), canvasMeanShift.getHeight());
							brushMeanShift.clear();
							canvasMeanShift.setOnMouseClicked((event) -> {
								allowClick(brushMeanShift, event);
								currentAnimation = new MeanShift(Integer.parseInt(inputField.getText()), graph, brushMeanShift);	// Create new animation for Mean Shift clustering
							});
						}
					});
				}
			});
			
			// Implementation of Previous Step button function
			Button btnPreviousStep = (Button) scene.lookup("#btnPreviousStep");
			btnPreviousStep.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					if (currentAnimation != null) {
						currentAnimation.previous();
					}
				}
			});
			// Implementation of Next Step button function
			Button btnNexStep = (Button) scene.lookup("#btnNextStep");
			btnNexStep.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					if (currentAnimation != null) {
						currentAnimation.next();
					}
				}
			});
			Button btnStart = (Button) scene.lookup("#btnStart");
			Button btnPause = (Button) scene.lookup("#btnPause");
			Button btnResume = (Button) scene.lookup("#btnResume");
			Button btnStop = (Button) scene.lookup("#btnStop");
			// Implementation of Start button function
			btnStart.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					if (currentAnimation != null) {
						currentAnimation.start();
						btnPause.setVisible(true);
						btnStop.setVisible(true);
						btnStart.setVisible(false);
					}
				}
			});
			// Implementation of Pause button function
			btnPause.setVisible(false);
			btnPause.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					currentAnimation.pause();
					btnResume.setVisible(true);
					btnPause.setVisible(false);
				}
			});
			// Implementation of Resume button function
			btnResume.setVisible(false);
			btnResume.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					currentAnimation.resume();
					btnPause.setVisible(true);
					btnResume.setVisible(false);
				}
			});
			// Implementation of Stop Step button function
			btnStop.setVisible(false);
			btnStop.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					currentAnimation.stop();
					btnStart.setVisible(true);
					btnPause.setVisible(false);
					btnResume.setVisible(false);
					btnStop.setVisible(false);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check whether the dialog input is an integer or not.
	 * @param text
	 * @return true - if text is integer
	 */
	private boolean isValid(String text) {
		try {
			Integer.parseInt(text);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void allowClick(Brush brush, MouseEvent event) {
		// Get old uncategorized node and clear it from canvas
		try {
			Node oldNode = graph.getUncategorizedNode();
			brush.clearPoint(oldNode.getX(), oldNode.getY());
		} catch (NullPointerException npe) { /* empty */ }
		// Set new uncategorized node and draw it
		graph.setUncategorizedNode(new Node(event.getX(), event.getY()));
		brush.drawPoint(event.getX(), event.getY(), Color.BLACK);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
