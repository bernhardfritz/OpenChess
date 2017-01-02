import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jetty.websocket.api.Session;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Player implements Observer {
	private Session session;
	private Color color;
	
	public Player(Session session) {
		this.session = session;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obs instanceof Board) {
			Board board = (Board) obs;
			if (board.getTurn() == color) {				
				ImmutableList<Movement> movements = board.getMovements();
				try {
					JsonElement element = new JsonParser().parse(movements.get(movements.size() - 1).toJson());
					JsonObject object = element.getAsJsonObject();
					object.addProperty("type", WebSocketMessageType.MOVEMENT.name());
					object.addProperty("token", board.getToken());
					session.getRemote().sendString(new Gson().toJson(object));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
