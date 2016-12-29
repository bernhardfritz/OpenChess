import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

public class ForsythEdwardsNotation {
	public static Board parse(String fen) throws Exception {
		Board board = new Board();
		if (StringUtils.countMatches(fen, "/") < 7) {
			throw new Exception();
		}
		String[] lines = fen.split("/");
		
		String suffix = null;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (i == 7 && line.contains(" ")) {
				suffix = line.split(" ", 2)[1]; 
				line = line.split(" ", 2)[0];
			}
			int j = 0;
			for (char ch : line.toCharArray()) {
				if (CharUtils.isAsciiAlphaUpper(ch)) {
					board.setSquare(File.values()[j], Rank.values()[Rank.values().length - i - 1], Piece.fromString(String.valueOf(ch)), Color.WHITE);
				} else if (CharUtils.isAsciiAlphaLower(ch)) {
					board.setSquare(File.values()[j], Rank.values()[Rank.values().length - i - 1], Piece.fromString(String.valueOf(ch)), Color.BLACK);
				} else if (CharUtils.isAsciiNumeric(ch)) {
					j += Character.digit(ch, 10);
					continue;
				} else {
					throw new Exception("illegal character");
				}
				j++;
			}
		}
		if (suffix != null) {
			String[] options = suffix.split(" ");
			Color turn = Color.fromString(options[0]);
			board.setTurn(turn);
			// TODO do something with other options
		}
		return board;
	}
}
