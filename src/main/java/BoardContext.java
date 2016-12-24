import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class BoardContext {
	Color player;
	List<Row> rows;
	static Map<Piece, String> glyphicons = new ImmutableMap.Builder<Piece, String>()
			.put(Piece.KING, "glyphicon-king")
			.put(Piece.QUEEN, "glyphicon-queen")
			.put(Piece.ROOK, "glyphicon-tower")
			.put(Piece.BISHOP, "glyphicon-bishop")
			.put(Piece.KNIGHT, "glyphicon-knight")
			.put(Piece.PAWN, "glyphicon-pawn")
			.build();
	
	BoardContext(Board board, Color player) {
		rows = new ArrayList<>(8);
		setup(board, player);
	}
	
	void setup(Board board, Color player) {
		Lists.reverse(Arrays.asList(Rank.values())).forEach((r) -> {
			List<Column> columns = new ArrayList<>(8);
			Arrays.asList(File.values()).forEach((f) -> {
				columns.add(new Column(board.get(f, r)));	
			});
			rows.add(new Row(columns));
		});
		
		if (Color.BLACK == player) {
			rows = Lists.reverse(rows);
		}
	}
	
	static class Row {
		List<Column> columns;
		
		Row(List<Column> columns) {
			this.columns = columns;
		}
	}
	
	static class Column {
		String piece;
		String color;
		String glyphicon;
		
		Column(Pair<Piece, Color> pair) {
			if (pair != null) {				
				Piece piece = pair.getLeft();
				Color color = pair.getRight();
				if (piece != null && color != null) {
					this.piece = piece.getValue();
					this.color = color.name().toLowerCase();
					this.glyphicon = glyphicons.get(piece);
				}
			}
		}
	}
}
