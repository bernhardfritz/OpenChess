import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@WebSocket
public class WebSocketHandler {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
    	JsonElement element = new JsonParser().parse(message);
		JsonObject object = element.getAsJsonObject();
		WebSocketMessageType type = WebSocketMessageType.valueOf(object.get("type").getAsString()); 
		switch(type) {
			case HASH: handleHashMessage(session, message); break;
			default: break;
		}
    }
    
    private void handleHashMessage(Session session, String message) {
    	JsonElement element = new JsonParser().parse(message);
		JsonObject object = element.getAsJsonObject();
		String hash = object.get("hash").getAsString();
    	Game game = Database.getInstance().getGame(hash);
    	game.addPlayer(new Player(session));
    }
}
