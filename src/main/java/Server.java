import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

public class Server {
	public static void main(String[] args) {
		staticFileLocation("/static");
        get("/boards/:id", (req, res) -> Board.create().toJson());
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
	}
}
