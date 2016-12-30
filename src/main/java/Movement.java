import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Movement {
	private File fromFile, toFile;
	private Rank fromRank, toRank;
	
	public Movement(File fromFile, Rank fromRank, File toFile, Rank toRank) {
		this.fromFile = fromFile;
		this.fromRank = fromRank;
		this.toFile = toFile;
		this.toRank = toRank;
	}
	
	public File getFromFile() {
		return fromFile;
	}

	public Rank getFromRank() {
		return fromRank;
	}

	public File getToFile() {
		return toFile;
	}

	public Rank getToRank() {
		return toRank;
	}
	
	public String toJson() {
		JsonObject root = new JsonObject();
		root.addProperty("from", fromFile.getValue() + fromRank.getValue());
		root.addProperty("to", toFile.getValue() + toRank.getValue());
		return new Gson().toJson(root);
	}
	
	public static Movement fromJson(String json) {
		JsonElement element = new JsonParser().parse(json);
		JsonObject object = element.getAsJsonObject();
		String from = object.get("from").getAsString(); // TODO check this, here is error
		String to = object.get("to").getAsString();
		File fromFile = File.valueOf(String.valueOf(Character.toUpperCase(from.charAt(0))));
		Rank fromRank = Rank.values()[Integer.valueOf(String.valueOf(from.charAt(1))) - 1];
		File toFile = File.valueOf(String.valueOf(Character.toUpperCase(to.charAt(0))));
		Rank toRank = Rank.values()[Integer.valueOf(String.valueOf(to.charAt(1))) - 1];
		return new Movement(fromFile, fromRank, toFile, toRank);
	}
}
