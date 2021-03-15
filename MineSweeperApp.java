package minesweeper;

import java.util.Optional;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/** For user to play the game of Minesweeper */
public class MineSweeperApp extends Application {
	
	private static BorderPane root;
	private static GameBoardArray board;
	private static Button exitBtn;
	
	/** main: launch the app */
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	/** start: initialize and organize panes and nodes */
	@SuppressWarnings("exports")
	@Override
	public void start(Stage primaryStage) {
		
		// create game pane
		board = new GameBoardArray();
		
		// create bottom pane
		exitBtn = getExitBt(primaryStage);
		HBox bottomPane = new HBox(10);
		bottomPane.setPadding(new Insets(5, 0, 15, 0));
		bottomPane.getChildren().addAll(board.getControlPane(), exitBtn);
		bottomPane.setAlignment(Pos.CENTER);

		// create root pane
		root = new BorderPane();
		root.setTop(board.getMessagePane());
		root.setCenter(board);
		root.setBottom(bottomPane);
		
		// scene and stage
		primaryStage.setTitle("MineSweeper Plus");
		primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	
	/** create "Exit" Button */
	private Button getExitBt(Stage primaryStage) {
		Button bt = new Button("Exit");
		bt.setPrefWidth(90);
		bt.setFocusTraversable(false);
		bt.setOnAction(e->{
			exitDialog(primaryStage);
		});
		return bt;
	}	
	
	
	/** create Exit Confirmation Dialog */
	private void exitDialog(Stage primaryStage) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Exit Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("\nDo you want to exit Minesweeper?");
		
		ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.YES);
		ButtonType buttonExit = new ButtonType("Exit", ButtonData.YES);
		alert.getButtonTypes().setAll(buttonCancel, buttonExit);
		
		Optional<ButtonType> choice = alert.showAndWait();
		if (choice.get() == buttonExit) {
			primaryStage.close();
		}
	}

}


