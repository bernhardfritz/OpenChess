public class Game {
	private String hash;
	private Board board;
	private Player white;
	private Player black;
	
	public Game(Board board) {
		this.board = board;
	}
	
	public static Game create() {
		Game game = new Game(Board.create());
		game.hash = Database.getInstance().putGame(game);
		return game;
	}
	
	public Board getBoard() {
		return board;
	}

	public String getHash() {
		return hash;
	}
	
	public void addPlayer(Player player) {
		if (white == null) {
			player.setColor(Color.WHITE);
			white = player;
			board.addObserver(white);
		} else if (black == null) {
			player.setColor(Color.BLACK);
			black = player;
			board.addObserver(black);
		}
	}
}
