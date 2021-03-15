package minesweeper;

@SuppressWarnings("unused")
class GameLevel {
	private String name;
	private int height;
	private int width;
	private int totalMine;
	
	/** no-args constructor */
	public GameLevel() {
		this("Normal", 9, 16, 20);
	}
	
	/** constructor with args */
	public GameLevel(String name, int height, int width, int totalMine) {
		this.name = name;
		this.height = height;
		this.width  = width;
		this.totalMine = totalMine;
	}
	
	public GameLevel Easy() {
		return new GameLevel("Easy", 9, 16, 16);
	}
	
	public GameLevel Normal() {
		return new GameLevel("Normal", 9, 16, 22);
	}
	
	public GameLevel Hard() {
		return new GameLevel("Hard", 9, 16, 28);
	}

	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getMineNums() {
		return this.totalMine;
	}
	
}
