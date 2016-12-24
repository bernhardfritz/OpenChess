import static spark.Spark.*;

import spark.ModelAndView;

public class Server {
	public static void main(String[] args) {
		staticFileLocation("/public");
        get("/", (req, res) -> new ModelAndView(new BoardContext(new Board(), Color.WHITE), "chess.mustache"), new MustacheTemplateEngine());
        exception(Exception.class, (exception, request, response) -> exception.printStackTrace());
	}
}
