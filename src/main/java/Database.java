import java.util.HashMap;
import java.util.Map;

public class Database {
	private Map<Long, Board> boards;
	private long boardId;
	
	private Database() {
		boards = new HashMap<>();
		boardId = 0L;
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
}
