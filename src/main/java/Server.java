import static spark.Spark.*;

public class Server {
	public static void main(String[] args) {
		staticFileLocation("/static");
		post("/boards", (req, res) -> {
			res.type("application/json");
			res.status(201);
			return Board.create().toJson();
		});
        get("/boards/:id", (req, res) -> {
        	res.type("application/json");
        	Board board = Database.getInstance().getBoard(Long.valueOf(req.params("id")));
        	if (board != null) {
        		return board.toJson();
        	}
        	res.status(404);
        	return "{}";
        });
        post("/boards/:id/movement", "application/json", (req, res) -> {
        	res.type("application/json");
        	Board board = Database.getInstance().getBoard(Long.valueOf(req.params("id")));
        	Movement movement = Movement.fromJson(req.body());
        	if (board != null && Rules.isLegitMovement(board, movement)) {
        		board.addMovement(movement);
        		Database.getInstance().putBoard(board);
        		res.status(201);
        		return "{}";
        	}
        	res.status(400);
        	return "{}";
        });
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
	}
}
