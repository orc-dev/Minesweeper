package minesweeper;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class GameBoardArray extends GridPane {
	private int height;
	private int width;
	private int totalMines;
	private int size;
	private GameLevel level;
	
	private Cell[] cells;
	private final int MINE = 9;
	private boolean gameDataInitialized;
	private boolean gameOver;
	
	private ComboBox<String> lvMenu;
	private Button newGameBtn;
	private Button replayBtn;
	private Label message;
	
	/** Constructor */
	public GameBoardArray() {
		level = new GameLevel().Normal();
		message = new Label();

		loadBoard(level);
		this.setHgap(2);
		this.setVgap(2);
		this.setPadding(new Insets(0, 20, 10, 20));
	}
	
	/** load the board as constructing an instance of it */
	private void loadBoard(GameLevel lv) {
		level = lv;
		height = level.getHeight();
		width = level.getWidth();
		totalMines = level.getMineNums();
		size = height * width;
		
		gameDataInitialized = false;
		gameOver = false;
		
		cells = new Cell[size];
		for (int idx = 0; idx < size; ++idx) {
			cells[idx] = new Cell();
			this.add(cells[idx], idx % width, idx / width);
			
			loadDefaultButton(idx);
			loadFlagingButton(idx);
			loadEventHandlerOnSquare(idx);
		}
		showMessage("start");
	}
	
	/** set event handler of default button */
	private void loadDefaultButton(int idx) {
		cells[idx].getDefaultBtn().setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (!gameDataInitialized) {
					initializeGameData(idx);
				}
				openCell(idx);
			}
			if (e.getButton() == MouseButton.SECONDARY) {
				if (countBoardFlag() < totalMines)
					cells[idx].flag();
			}
			if (!gameOver) {
				showMessage("count");
			}
		});	
	}
	
	/** set event handler of flaging button */
	private void loadFlagingButton(int idx) {
		cells[idx].getFlagingBtn().setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY){
				cells[idx].removeFlag();
				showMessage("count");
			}
		});
	}
	
	/** 
	 * set event handler on Numbered-Cells:
	 * if (nearby flag   = number), left-click  -> open all nearby default cells
	 * if (nearby closed = number), right-click -> flag all nearby default cells
	 */ 
	private void loadEventHandlerOnSquare(int idx) {
		cells[idx].setOnMouseClicked(e -> {
			// right-click: flag all nearby default button if possible
			if (cells[idx].isNumber() && 
				e.getButton() == MouseButton.SECONDARY && 
				countLocalUnopened(idx) == cells[idx].getData()) {
					getNeighborIndexes(idx).stream()
					.filter(index -> cells[index].isDefault())
					.forEach(index -> cells[index].flag());
			}
			// left-click: open all nearby default button if possible
			if (cells[idx].isNumber() && 
				e.getButton() == MouseButton.PRIMARY &&
				countLocalFlag(idx) == cells[idx].getData()) {
					getNeighborIndexes(idx).stream()
					.filter(index -> cells[index].isDefault())
					.forEach(index -> openCell(index));
			}
			// if hit a mine...
			if (!gameOver)
				showMessage("count");
		});	
		
	}
	
	
	/** loading mines and numbers data */
	private void initializeGameData(int idx) {
		// Load mine cells
		generateMineSet(idx).forEach(e -> cells[e].loadData(MINE));
		
		// Load other cells
		for (int i = 0; i < size; ++i) {
			if (!cells[i].isMine())
				 cells[i].loadData(countLocalMine(i));
		}
		gameDataInitialized = true;
	}
	
	
	/** generate required quantity of mines */
	private HashSet<Integer> generateMineSet(int idx) {
		HashSet<Integer> mineSet = new HashSet<>();
		mineSet.add(idx);
		
		Random randGen = new Random();
		while (mineSet.size() < totalMines + 1) {
			mineSet.add(randGen.nextInt(size));
		}
		mineSet.remove(idx);
		return mineSet;
	}
	
	/** return the number of nearby 'mines' of a cell */
	private int countLocalMine(int idx) {
		return (int) getNeighborIndexes(idx).stream()
			    .filter(e -> cells[e].isMine()).count();
	}
	
	/** return the number of nearby 'flags' of a cell */
	private int countLocalFlag(int idx) {
		return (int) getNeighborIndexes(idx).stream()
				.filter(e -> cells[e].isFlag()).count();
	}
	
	/** return the number of nearby 'default' buttons of a cell */
	private int countLocalDefault(int idx) {
		return (int) getNeighborIndexes(idx).stream()
				.filter(e -> cells[e].isDefault()).count();
	}
	
	/** return the number of nearby 'unopened' cells*/
	private int countLocalUnopened(int idx) {
		return countLocalFlag(idx) + countLocalDefault(idx);
	}
	
	/** return the indexes of all nearby cells as a list */
	private List<Integer> getNeighborIndexes(int idx) {
		int row = idx / width;
		int col = idx % width;
		
		int[][] neighborPoints = new int[][]{
			{row - 1, col - 1},
			{row - 1, col    },
			{row - 1, col + 1},
			{row    , col - 1},
			{row    , col + 1},
			{row + 1, col - 1},
			{row + 1, col    },
			{row + 1, col + 1},
		};
		
		return Stream.of(neighborPoints)
			   .filter(point -> isOnBoard(point))
			   .map(point -> point[0] * width + point[1])
			   .collect(Collectors.toList());
	}
		
	/** check whether a given cell's coordinates is on board */
	private boolean isOnBoard(int[] point) {
		return (point[0] >= 0 && point[0] < height &&
				point[1] >= 0 && point[1] < width);
	}
		
	/** open the cell and check win or lose */
	private void openCell(int idx) {
		
		switch(cells[idx].getData()) {
			case MINE:  // Hit a mine: Game Over
				cells[idx].setRed(true); 
				showAll("lose");
				return;
				
			case 0:
				cells[idx].open();
				getNeighborIndexes(idx).stream()
					.filter(e -> !cells[e].isOpen())
					.forEach(e -> openCell(e));
				break;
				
			default:
				cells[idx].open();
				break;
		}
		
		// check: if all non-mine-cells are open -> WIN
		if (countBoardBtn() == totalMines) 
			showAll("win");
	}

	
	/** show all cells as game over */
	private void showAll(String result) {
		showMessage(result);
		newGameBtn.setDisable(false);
		replayBtn.setDisable(false);
		Stream.of(cells).forEach(e -> e.open());
		gameOver = true;
	}
	
	private int countBoardDefault() {
		return (int) Stream.of(cells).filter(e -> e.isDefault()).count();
	}
	
	private int countBoardFlag() {
		return (int) Stream.of(cells).filter(e -> e.isFlag()).count();
	}
	
	private int countBoardBtn() {
		return countBoardDefault() + countBoardFlag();
	}

	public void restartBoard() {
		gameOver = false;
		showMessage("start");
		Stream.of(cells).forEach(e -> e.reset());
	}
	
	public StackPane getMessagePane() {
		StackPane messagePane = new StackPane();
		messagePane.setPadding(new Insets(15, 10, 10, 10));
		messagePane.setPrefHeight(60);
		messagePane.getChildren().add(message);
		return messagePane;
	}
	
	
	private void showMessage(String input) { 
		switch(input) {
			case "start": message.setText("PRESS ANY BUTTON TO START THE GAME");
						  message.setTextFill(Color.rgb( 40, 190, 185));
				          break;
				          
			case "win":   message.setText("YOU WIN! CONGRATULATIONS!");
						  message.setTextFill(Color.rgb(  0, 150, 150));
				          break;
				          
			case "lose":  message.setText("GAME OVER!");
						  message.setTextFill(Color.rgb(150, 150, 250));
				          break;
				          
			case "count": message.setText("MINE: " + totalMines
				          + "   FLAG: " + countBoardFlag());
						  message.setTextFill(Color.rgb(  0,   0, 150));
				          break;
				          
			default:      message.setText("* * *");
				          break;
		}
		message.setFont(Font.font("Console", FontWeight.BOLD, 14));
	}
	
	private void loadLevelMenu() {
		String[] levelNames = {"Easy", "Normal", "Hard"};
		ObservableList<String> items = FXCollections.observableArrayList(levelNames);
		lvMenu = new ComboBox<>(items);
		lvMenu.setPrefWidth(90);
		lvMenu.getSelectionModel().select(1);
		lvMenu.setFocusTraversable(false);
		lvMenu.setOnAction(e -> {
			switch(items.indexOf(lvMenu.getValue())) {
				case 0: level = new GameLevel().Easy();
						break;
				case 1: level = new GameLevel().Normal();
						break;
				case 2: level = new GameLevel().Hard();
						break;
			}
			loadBoard(level);
		});
	}
	
	/** create "New Game" Button */
	private void loadNewGameBt() {
		newGameBtn = new Button("New Game");
		newGameBtn.setPrefWidth(90);
		newGameBtn.setFocusTraversable(false);
		newGameBtn.setOnAction(e -> {
			loadBoard(level);
		});
	}
	
	
	/** create "Replay" Button */
	private void loadReplayBt() {
		replayBtn = new Button("Replay");
		replayBtn.setPrefWidth(90);
		replayBtn.setFocusTraversable(false);
		replayBtn.setOnAction(e -> {
			restartBoard();
		});
	}
		
	public HBox getControlPane() {
		loadLevelMenu();
		loadNewGameBt();
		loadReplayBt();
		
		HBox controlPane = new HBox(10);
		controlPane.setPadding(new Insets(0, 0, 0, 0));
		controlPane.getChildren().addAll(lvMenu, newGameBtn, replayBtn);
		controlPane.setAlignment(Pos.CENTER);
		return controlPane;
	}
	
}
