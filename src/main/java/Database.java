import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

public class Database {
	private long boardId;
	private Map<Long, Board> boards;
	private Map<String, Game> games;
	
	private Database() {
		boardId = 0L;
		boards = new HashMap<>();
		games = new HashMap<>();
	}
	
	private static class LazyHolder {
		private static final Database INSTANCE = new Database();
	}
	
	public static Database getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public long putBoard(Board board) {
		long id = board.getId();
		if (id == -1) {
			id = boardId++;
		}
		boards.put(id, board);
		return id;
	}
	
	public Board getBoard(long id) {
		return boards.get(id);
	}
	
	public String putGame(Game game) {
		String hash = RandomStringUtils.randomAlphabetic(8);
		if (games.containsKey(hash)) {
			return putGame(game);
		}
		games.put(hash, game);
		return hash;
	}
	
	public Game getGame(String hash) {
		return games.get(hash);
	}
}
