import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import spark.ModelAndView;

public class Server {
	private static final boolean localhost = true;
	
	public static void main(String[] args) {
		if (localhost) {
		    String projectDir = System.getProperty("user.dir");
		    String staticDir = "/src/main/resources/public";
		    staticFiles.externalLocation(projectDir + staticDir);
		} else {
		    staticFiles.location("/public");
		}
		webSocket("/ws", WebSocketHandler.class);
		get("/ws", (req, res) -> {
			res.status(101);
			return null;
		});
		get("/", (req, res) -> {
			Game game = Game.create();
			res.redirect("/" + game.getHash());
			return null;
		});
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
        get("/:hash", (req, res) -> {
        	String hash = req.params("hash");
        	if (hash.length() != 8 || !StringUtils.isAlpha(hash)) {
        		halt();
        	}
        	Game game = Database.getInstance().getGame(hash);
        	Map<String, Object> model = new HashMap<>();
            model.put("content", "<div id=\"chessboard\" rel=\"" + game.getBoard().getId() + "\"/>");
            return new ModelAndView(model, "in"
            		+ "dex.mustache");
        }, new MustacheTemplateEngine());
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
	}
}
