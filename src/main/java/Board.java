import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Board extends Observable {
	private long id;
	private Color turn;
	private Color check;
	private Map<Pair<File, Rank>, Pair<Piece, Color>> squares;
	private List<Movement> movements;
	private String token; // token is only valid for one turn
	private boolean once;
	
	public Board() {
		id = -1;
		turn = Color.WHITE;
		check = null;
		squares = new HashMap<>();
		movements = new ArrayList<>();
		refreshToken();
		once = true;
	}
	
	public static Board create() {
		Board board = null;
		try {
			board = ForsythEdwardsNotation.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		} catch(Exception e) {
			// do nothing
		}
		board.id = Database.getInstance().putBoard(board);
		return board;
	}
	
	public Board(Board board) {
		this.id = board.id;
		this.turn = board.turn;
		this.check = board.check;
		squares = new HashMap<>();
		for (Entry<Pair<File, Rank>, Pair<Piece, Color>> entry : board.squares.entrySet()) {
			squares.put(entry.getKey(), entry.getValue());
		}
		movements = new ArrayList<>();
		for (Movement movement : movements) {
			movements.add(movement);
		}
		this.token = board.token;
		this.once = board.once;
	}
	
	public long getId() {
		return id;
	}
	
	public Color getTurn() {
		return turn;
	}
	
	public void setTurn(Color turn) {
		this.turn = turn;
	}
	
	public Color getCheck() {
		return check;
	}
	
	public void setCheck(Color color) {
		check = color;
	}
	
	public Pair<Piece, Color> getSquare(File file, Rank rank) {
		return squares.get(new ImmutablePair<>(file, rank));
	}
	
	public void setSquare(File file, Rank rank, Piece piece, Color color) {
		squares.put(new ImmutablePair<>(file, rank), new ImmutablePair<>(piece, color));
	}
	
	public Pair<File, Rank> getPosition(Pair<Piece, Color> uniquePiece) {
		for (Entry<Pair<File, Rank>, Pair<Piece, Color>> entry : squares.entrySet()) {
			if (uniquePiece.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public ImmutableList<Movement> getMovements() {
		return ImmutableList.copyOf(movements);
	}
	
	public void addMovement(Movement movement) {
		movements.add(movement);
		Pair<Piece, Color> square = squares.put(new ImmutablePair<File, Rank>(movement.getFromFile(), movement.getFromRank()), null);
		squares.put(new ImmutablePair<File, Rank>(movement.getToFile(), movement.getToRank()), square);
		turn = turn.getOpposite();
		refreshToken();
		setChanged();
	    notifyObservers();
	}
	
	public String getToken() {
		return token;
	}
	
	public void refreshToken() {
		token = RandomStringUtils.randomAlphabetic(8);
	}
	
	public boolean isThreatened(File file, Rank rank) {
		Pair<Piece, Color> square = getSquare(file, rank);
		if (square == null) {
			return false;
		}
		Color opponent = square.getRight().getOpposite();
		int fileIndex = ArrayUtils.indexOf(File.values(), file);
		int rankIndex = ArrayUtils.indexOf(Rank.values(), rank);
		
		// threatened by knight
		int[] deltas = { -2, -1, 1, 2 };
		for (int df : deltas) {
			int targetFileIndex = fileIndex + df;
			if (targetFileIndex >= 0 && targetFileIndex < File.values().length) {				
				for(int dr : deltas) {
					int targetRankIndex = rankIndex + dr;
					if (targetRankIndex >= 0 && targetRankIndex < Rank.values().length) {
						Pair<Piece, Color> targetSquare = getSquare(File.values()[targetFileIndex], Rank.values()[targetRankIndex]);
						if (targetSquare != null && targetSquare.getRight() == opponent && Piece.KNIGHT == targetSquare.getLeft()) {
							return true;
						}
					}
				}
			}
		}
		
		// threatened horizontally
		File[] subFileArrayLeft = ArrayUtils.subarray(File.values(), ArrayUtils.indexOf(File.values(), File.A), ArrayUtils.indexOf(File.values(), file));
		ArrayUtils.reverse(subFileArrayLeft);
		File[] subFileArrayRight = ArrayUtils.subarray(File.values(), ArrayUtils.indexOf(File.values(), file) + 1, ArrayUtils.indexOf(File.values(), File.H));
		for (File f : subFileArrayLeft) {
			Pair<Piece, Color> targetSquare = getSquare(f, rank);
			if (targetSquare != null) {
				Piece targetPiece = targetSquare.getLeft();
				if (targetSquare.getRight() == opponent && (Piece.KING == targetPiece && ArrayUtils.indexOf(subFileArrayLeft, f) == 0 || Piece.QUEEN == targetPiece || Piece.ROOK == targetPiece)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (File f : subFileArrayRight) {
			Pair<Piece, Color> targetSquare = getSquare(f, rank);
			if (targetSquare != null) {
				Piece targetPiece = targetSquare.getLeft();
				if (targetSquare.getRight() == opponent && (Piece.KING == targetPiece && ArrayUtils.indexOf(subFileArrayRight, f) == 0 || Piece.QUEEN == targetPiece || Piece.ROOK == targetPiece)) {
					return true;
				} else {
					break;
				}
			}
		}
		
		// threatened vertically
		Rank[] subRankArrayTop = ArrayUtils.subarray(Rank.values(), ArrayUtils.indexOf(Rank.values(), rank) + 1, ArrayUtils.indexOf(Rank.values(), Rank.EIGHT));
		Rank[] subRankArrayBottom = ArrayUtils.subarray(Rank.values(), ArrayUtils.indexOf(Rank.values(), Rank.ONE), ArrayUtils.indexOf(Rank.values(), rank));
		ArrayUtils.reverse(subRankArrayBottom);
		for (Rank r : subRankArrayTop) {
			Pair<Piece, Color> targetSquare = getSquare(file, r);
			if (targetSquare != null) {
				Piece targetPiece = targetSquare.getLeft();
				if (targetSquare.getRight() == opponent && (Piece.KING == targetPiece && ArrayUtils.indexOf(subRankArrayTop, r) == 0 || Piece.QUEEN == targetPiece || Piece.ROOK == targetPiece)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (Rank r : subRankArrayBottom) {
			Pair<Piece, Color> targetSquare = getSquare(file, r);
			if (targetSquare != null) {
				Piece targetPiece = targetSquare.getLeft();
				if (targetSquare.getRight() == opponent && (Piece.KING == targetPiece && ArrayUtils.indexOf(subRankArrayBottom, r) == 0 || Piece.QUEEN == targetPiece || Piece.ROOK == targetPiece)) {
					return true;
				} else {
					break;
				}
			}
		}
		
		// threatened diagonally
		// top right
		if (subFileArrayRight.length < subRankArrayTop.length) {
			for (File f : subFileArrayRight) {
				Rank r = subRankArrayTop[ArrayUtils.indexOf(subFileArrayRight, f)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subFileArrayRight, f) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		} else {
			for (Rank r : subRankArrayTop) {
				File f = subFileArrayRight[ArrayUtils.indexOf(subRankArrayTop, r)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subRankArrayTop, r) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		}
		// bottom right
		if (subFileArrayRight.length < subRankArrayBottom.length) {
			for (File f : subFileArrayRight) {
				Rank r = subRankArrayBottom[ArrayUtils.indexOf(subFileArrayRight, f)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subFileArrayRight, f) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		} else {
			for (Rank r : subRankArrayBottom) {
				File f = subFileArrayRight[ArrayUtils.indexOf(subRankArrayBottom, r)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subRankArrayBottom, r) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		}
		// bottom left
		if (subFileArrayLeft.length < subRankArrayBottom.length) {
			for (File f : subFileArrayLeft) {
				Rank r = subRankArrayBottom[ArrayUtils.indexOf(subFileArrayLeft, f)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subFileArrayLeft, f) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		} else {
			for (Rank r : subRankArrayBottom) {
				File f = subFileArrayLeft[ArrayUtils.indexOf(subRankArrayBottom, r)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subRankArrayBottom, r) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		}
		// top left
		if (subFileArrayLeft.length < subRankArrayTop.length) {
			for (File f : subFileArrayLeft) {
				Rank r = subRankArrayTop[ArrayUtils.indexOf(subFileArrayLeft, f)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subFileArrayLeft, f) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		} else {
			for (Rank r : subRankArrayTop) {
				File f = subFileArrayLeft[ArrayUtils.indexOf(subRankArrayTop, r)];
				Pair<Piece, Color> targetSquare = getSquare(f, r);
				if (targetSquare != null) {
					Piece targetPiece = targetSquare.getLeft();
					if (targetSquare.getRight() == opponent && ((Piece.KING == targetPiece || Piece.PAWN == targetPiece) && ArrayUtils.indexOf(subRankArrayTop, r) == 0 || Piece.BISHOP == targetPiece)) {
						return true;
					} else {
						break;
					}
				}
			}
		}
		
		return false;
	}
	
	public String toJson() {
		JsonObject root = new JsonObject();
		root.addProperty("id", id);
		JsonArray pieces = new JsonArray();
		for (File f : File.values()) {
			for (Rank r : Rank.values()) {
				Pair<Piece, Color> pair = squares.get(new ImmutablePair<>(f, r));
				if (pair != null) {
					JsonObject piece = new JsonObject();
					piece.addProperty("type", pair.getLeft().name());
					piece.addProperty("color", pair.getRight().name());
					piece.addProperty("position", f.getValue() + r.getValue());
					pieces.add(piece);
				}
			}
		}
		if (once) {
			root.addProperty("token", token);
			once = false;
		}
		root.add("pieces", pieces);
		return new Gson().toJson(root);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Lists.reverse(Arrays.asList(Rank.values())).forEach((r) -> Arrays.asList(File.values()).forEach((f) -> sb.append(squares.get(new ImmutablePair<File, Rank>(f, r)))));
		return sb.toString();
	}
	
}

