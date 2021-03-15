package minesweeper;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/** 
 *  create the cells for minesweeper; 
 *  provide relative method to operate these cells;
 *  cells are:
 *  1. small square of StackPane with numbers
 *     or symbols to indicating nearby mines;
 *  2. two buttons covered on each square
 */
class Cell extends StackPane {
	
	private Button defaultBtn;
	private Button flagingBtn;
	private Label icon;
	private Rectangle square;
	private int numData;
	private final Color GRAY = Color.rgb(200, 200, 200);
	private final Color RED  = Color.rgb(240,   0,   0);
	
	/** constructor */
	public Cell() {
		this.setPrefHeight(25);
		this.setPrefWidth(25);
		numData = -1;
		
		loadSquare();
		loadLabel();
		loadDefaultButton();
		loadFlagingButton();
		
		this.getChildren().addAll(square,  
			icon, flagingBtn, defaultBtn);
	}
	
	/** load square when creating a cell */
	private void loadSquare() {
		square = new Rectangle(25, 25);
		square.setFill(GRAY);
		square.setStroke(Color.rgb(150, 150, 150));
		square.setStrokeWidth(1);
	}

	/** load label when creating a cell */
	private void loadLabel() {
		icon = new Label();
		icon.setText("");
		icon.setFont(Font.font("Courier", FontWeight.BOLD, 15));
		icon.setVisible(false);
	}
	
	/** load default button */
	private void loadDefaultButton() {
		defaultBtn = new Button("");
		defaultBtn.setPrefSize(26, 26);
		defaultBtn.setStyle("-fx-background-radius: 0;");
		defaultBtn.setFocusTraversable(false);
	}
	
	/** load flaging button */
	private void loadFlagingButton() {
		//flagingBtn = new Button("⚑");
		flagingBtn = new Button("X");
		flagingBtn.setPrefSize(26, 26);
		flagingBtn.setStyle("-fx-background-radius: 0; "
				+ "-fx-text-fill:red; -fx-font-weight:bold;");
		flagingBtn.setFocusTraversable(false);
		flagingBtn.setVisible(false);
	}
	
	/** return default button */
	public Button getDefaultBtn() {
		return defaultBtn;
	}
	
	/** return flaging button */
	public Button getFlagingBtn() {
		return flagingBtn;
	}
	
	/** load data of nearby mines */
	public void loadData(int data) {
		numData = data;
		switch(data) {
			case 1: icon.setText("1");
				    icon.setTextFill(Color.BLUE);
				    return;
		
			case 2: icon.setText("2");
				    icon.setTextFill(Color.DARKGREEN);
				    return;	
				
			case 3: icon.setText("3");
				    icon.setTextFill(Color.DARKRED);
				    return;
				
			case 4: icon.setText("4");
				    icon.setTextFill(Color.DARKBLUE);
				    return;
				
			case 5: icon.setText("5");
				    icon.setTextFill(Color.RED);
			        return;
				
			case 6: icon.setText("6");
				    icon.setTextFill(Color.DARKVIOLET);
				    return;
				
			case 7: icon.setText("7");
				    icon.setTextFill(Color.DARKORANGE);
				    return;
				
			case 8: icon.setText("8");
				    icon.setTextFill(Color.WHITE);
				    return;
				
			case 9: icon.setText("⬤");
				    icon.setTextFill(Color.BLACK);
				    return;
		}
	}
	
	/** return numData */
	public int getData() {
		return numData;
	}
	
	/** set the background square color 'red' or 'gray' */
	public void setRed(boolean hitTheMine) {
		if (hitTheMine) 
			square.setFill(RED);
		else
			square.setFill(GRAY);
	} 
	
	/** 'open' a cell */
	public void open() {
		icon.setVisible(true);
		flagingBtn.setVisible(false);
		defaultBtn.setVisible(false);
	}
	
	/** 'flag' a cell */
	public void flag() {
		flagingBtn.setVisible(true);
		defaultBtn.setVisible(false);
	}
	
	/** remove the flag of the cell */
	public void removeFlag() {
		defaultBtn.setVisible(true);
		flagingBtn.setVisible(false);
	}
	
	/** reset the cell */
	public void reset() {
		this.setRed(false);
		icon.setVisible(false);
		flagingBtn.setVisible(false);
		defaultBtn.setVisible(true);
	}
	
	/** return true if the cell is 'open' */
	public boolean isOpen() {
		return icon.isVisible();
	}
	
	/** return true if the cell is covered by a default button */
	public boolean isDefault() {
		return defaultBtn.isVisible();
	}
	
	/** return true if the cell is covered by a flaging button */
	public boolean isFlag() {
		return flagingBtn.isVisible();
	}
	
	/** return true if the cell is a mine */
	public boolean isMine() {
		return (numData == 9);
	}
	
	/** return true if the cell near at leat a mine */
	public boolean isNumber() {
		return (numData > 0 && numData < 9);
	}
	
}