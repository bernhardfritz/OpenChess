import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class Board {
	Map<Pair<File, Rank>, Pair<Piece, Color>> board;
	
	public Board() {
		board = new HashMap<>();
		setup();
	}
	
	public void setup() {
		// kings
		board.put(new ImmutablePair<>(File.E, Rank.ONE), new ImmutablePair<>(Piece.KING, Color.WHITE));
		board.put(new ImmutablePair<>(File.E, Rank.EIGHT), new ImmutablePair<>(Piece.KING, Color.BLACK));
		
		// queens
		board.put(new ImmutablePair<>(File.D, Rank.ONE), new ImmutablePair<>(Piece.QUEEN, Color.WHITE));
		board.put(new ImmutablePair<>(File.D, Rank.EIGHT), new ImmutablePair<>(Piece.QUEEN, Color.BLACK));
		
		// rooks
		board.put(new ImmutablePair<>(File.A, Rank.ONE), new ImmutablePair<>(Piece.ROOK, Color.WHITE));
		board.put(new ImmutablePair<>(File.H, Rank.ONE), new ImmutablePair<>(Piece.ROOK, Color.WHITE));
		board.put(new ImmutablePair<>(File.A, Rank.EIGHT), new ImmutablePair<>(Piece.ROOK, Color.BLACK));
		board.put(new ImmutablePair<>(File.H, Rank.EIGHT), new ImmutablePair<>(Piece.ROOK, Color.BLACK));
		
		// bishops
		board.put(new ImmutablePair<>(File.C, Rank.ONE), new ImmutablePair<>(Piece.BISHOP, Color.WHITE));
		board.put(new ImmutablePair<>(File.F, Rank.ONE), new ImmutablePair<>(Piece.BISHOP, Color.WHITE));
		board.put(new ImmutablePair<>(File.C, Rank.EIGHT), new ImmutablePair<>(Piece.BISHOP, Color.BLACK));
		board.put(new ImmutablePair<>(File.F, Rank.EIGHT), new ImmutablePair<>(Piece.BISHOP, Color.BLACK));
		
		// knights
		board.put(new ImmutablePair<>(File.B, Rank.ONE), new ImmutablePair<>(Piece.KNIGHT, Color.WHITE));
		board.put(new ImmutablePair<>(File.G, Rank.ONE), new ImmutablePair<>(Piece.KNIGHT, Color.WHITE));
		board.put(new ImmutablePair<>(File.B, Rank.EIGHT), new ImmutablePair<>(Piece.KNIGHT, Color.BLACK));
		board.put(new ImmutablePair<>(File.G, Rank.EIGHT), new ImmutablePair<>(Piece.KNIGHT, Color.BLACK));
		
		// pawns
		Arrays.asList(File.values()).forEach((f) -> board.put(new ImmutablePair<>(f, Rank.TWO), new ImmutablePair<>(Piece.PAWN, Color.WHITE)));
		Arrays.asList(File.values()).forEach((f) -> board.put(new ImmutablePair<>(f, Rank.SEVEN), new ImmutablePair<>(Piece.PAWN, Color.BLACK)));
	}
	
	public Pair<Piece, Color> get(File file, Rank rank) {
		return board.get(new ImmutablePair<>(file, rank));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Lists.reverse(Arrays.asList(Rank.values())).forEach((r) -> Arrays.asList(File.values()).forEach((f) -> sb.append(board.get(new ImmutablePair<File, Rank>(f, r)))));
		return sb.toString();
	}
}
