import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

public class Rules {
	public static boolean isLegitMovement(Board board, Movement movement) {
		Pair<Piece, Color> fromSquare = getFromSquare(board, movement);
		boolean legitMovement = false;
		if (fromSquare != null && fromSquare.getLeft() != null) {
			switch(fromSquare.getLeft()) {
				case KING: legitMovement = isLegitKingMovement(board, movement); break;
				case QUEEN: legitMovement = isLegitQueenMovement(board, movement); break;
				case ROOK: legitMovement = isLegitRookMovement(board, movement); break;
				case BISHOP: legitMovement = isLegitBishopMovement(board, movement); break;
				case KNIGHT: legitMovement = isLegitKnightMovement(board, movement); break;
				case PAWN: legitMovement = isLegitPawnMovement(board, movement); break;
			}
			legitMovement = legitMovement && fromSquare.getRight() == board.getTurn() && !isCollision(board, movement) && !isCheckAfterMovement(board, movement);
		}
		return legitMovement;
	}
	
	private static boolean isLegitKingMovement(Board board, Movement movement) {
		int absDeltaFile = Math.abs(getDeltaFile(movement));
		int absDeltaRank = Math.abs(getDeltaRank(movement));
		return (absDeltaFile == 1 || absDeltaRank == 1) && (absDeltaFile + absDeltaRank <= 2) || isCastling(board, movement);
	}
	
	private static boolean isLegitQueenMovement(Board board, Movement movement) {
		return isLegitRookMovement(board, movement) || isLegitBishopMovement(board, movement);
	}

	private static boolean isLegitRookMovement(Board board, Movement movement) {
		int absDeltaFile = Math.abs(getDeltaFile(movement));
		int absDeltaRank = Math.abs(getDeltaRank(movement));
		return (absDeltaFile == 0 && absDeltaRank >= 1) || (absDeltaFile >= 1 && absDeltaRank == 0);
	}

	private static boolean isLegitBishopMovement(Board board, Movement movement) {
		int deltaFile = getDeltaFile(movement);
		int deltaRank = getDeltaRank(movement);
		int absDeltaFile = Math.abs(deltaFile);
		int absDeltaRank = Math.abs(deltaRank);
		return absDeltaFile >= 1 && absDeltaRank >= 1 && deltaFile == deltaRank;
	}
	
	private static boolean isLegitKnightMovement(Board board, Movement movement) {
		int absDeltaFile = Math.abs(getDeltaFile(movement));
		int absDeltaRank = Math.abs(getDeltaRank(movement));
		return (absDeltaFile == 1 && absDeltaRank == 2) || (absDeltaFile == 2 && absDeltaRank == 1);
	}
	
	private static boolean isLegitPawnMovement(Board board, Movement movement) {
		Pair<Piece, Color> fromSquare = getFromSquare(board, movement);
		Color color = fromSquare.getRight();
		int deltaRank = getDeltaRank(movement);
		int absDeltaFile = Math.abs(getDeltaFile(movement));
		return (Color.WHITE == color ? deltaRank == -1 || (Rank.TWO == movement.getFromRank() && deltaRank == -2) : deltaRank == 1 || (Rank.SEVEN == movement.getFromRank() && deltaRank == 2)) && (isCapture(board, movement) ? absDeltaFile == 1 : absDeltaFile == 0);  
	}
	
	private static Pair<Piece, Color> getFromSquare(Board board, Movement movement) {
		return board.getSquare(movement.getFromFile(), movement.getFromRank());
	}
	
	private static Pair<Piece, Color> getToSquare(Board board, Movement movement) {
		return board.getSquare(movement.getToFile(), movement.getToRank());
	}
	
	private static int getDeltaFile(Movement movement) {
		return ArrayUtils.indexOf(File.values(), movement.getFromFile()) - ArrayUtils.indexOf(File.values(), movement.getToFile());
	}
	
	private static int getDeltaRank(Movement movement) {
		return ArrayUtils.indexOf(Rank.values(), movement.getFromRank()) - ArrayUtils.indexOf(Rank.values(), movement.getToRank());
	}
	
	private static boolean isCastling(Board board, Movement movement) {
		Pair<Piece, Color> fromSquare = getFromSquare(board, movement);
		Color color = fromSquare.getRight();
		int deltaFile = getDeltaFile(movement);
		int absDeltaFile = Math.abs(deltaFile);
		ImmutableList<Movement> movements = board.getMovements();
		boolean kingHasNeverMoved = true;
		for (Movement m : movements) {
			if (Color.WHITE == color && File.E == m.getFromFile() && Rank.ONE == m.getFromRank() || Color.BLACK == color && File.E == m.getFromFile() && Rank.EIGHT == m.getFromRank()) {
				kingHasNeverMoved = false;
				break;
			}
		}
		boolean rookHasNeverMoved = true;
		for (Movement m : movements) {
			if (deltaFile < 0) {
				if (Color.WHITE == color && File.A == m.getFromFile() && Rank.ONE == m.getFromRank() || Color.BLACK == color && File.A == m.getFromFile() && Rank.EIGHT == m.getFromRank()) {
					rookHasNeverMoved = false;
					break;
				}
			} else {
				if (Color.WHITE == color && File.H == m.getFromFile() && Rank.ONE == m.getFromRank() || Color.BLACK == color && File.H == m.getFromFile() && Rank.EIGHT == m.getFromRank()) {
					rookHasNeverMoved = false;
					break;
				}
			}
		}
		return Piece.KING == fromSquare.getLeft() && absDeltaFile == 2 && kingHasNeverMoved && rookHasNeverMoved; 
	}
	
	private static boolean isCapture(Board board, Movement movement) {
		Pair<Piece, Color> fromSquare = getFromSquare(board, movement);
		Pair<Piece, Color> toSquare = getToSquare(board, movement);
		return (toSquare != null && fromSquare.getRight() != toSquare.getRight()) || isEnPassantCapture(board, movement);
	}
	
	private static boolean isEnPassantCapture(Board board, Movement movement) {
		Pair<Piece, Color> fromSquare = getFromSquare(board, movement);
		Pair<Piece, Color> toSquare = getToSquare(board, movement);
		int absDeltaFile = Math.abs(getDeltaFile(movement));
		ImmutableList<Movement> movements = board.getMovements();
		Movement previousMovement = movements.size() > 0 ? movements.get(board.getMovements().size() - 1) : null;
		if (previousMovement != null) {
			Pair<Piece, Color> previousToSquare = getToSquare(board, previousMovement);
			int previousDeltaRank = getDeltaRank(previousMovement);
			int previousAbsDeltaFile = Math.abs(getDeltaFile(previousMovement));			
			return Piece.PAWN == fromSquare.getLeft() && toSquare == null && absDeltaFile == 1 && Piece.PAWN == previousToSquare.getLeft() && (Color.WHITE == fromSquare.getRight() ? Rank.SEVEN == previousMovement.getFromRank() && previousDeltaRank == 2 && previousAbsDeltaFile == 0 && Rank.FIVE == movement.getFromRank(): Rank.TWO == previousMovement.getFromRank() && previousDeltaRank == -2 && previousAbsDeltaFile == 0 && Rank.FOUR == movement.getFromRank());
		}
		return false;
	}
	
	private static boolean isCollision(Board board, Movement movement) {
		Pair<Piece, Color> fromSquare = getFromSquare(board, movement);
		if (Piece.KNIGHT == fromSquare.getLeft()) {
			return false;
		}
		int deltaFile = getDeltaFile(movement);
		int deltaRank = getDeltaRank(movement);
		int absDeltaFile = Math.abs(deltaFile);
		int absDeltaRank = Math.abs(deltaRank);
		boolean horizontal = absDeltaFile > 0;
		boolean vertical = absDeltaRank > 0;
		boolean diagonal = horizontal && vertical;
		int fromFileIndex = ArrayUtils.indexOf(File.values(), movement.getFromFile());
		int fromRankIndex = ArrayUtils.indexOf(Rank.values(), movement.getFromRank());
		int toFileIndex = ArrayUtils.indexOf(File.values(), movement.getToFile());
		int toRankIndex = ArrayUtils.indexOf(Rank.values(), movement.getToRank());
		File[] subFileArray = ArrayUtils.subarray(File.values(), deltaFile < 0 ? fromFileIndex + 1 : toFileIndex + 1, deltaFile < 0 ? toFileIndex : fromFileIndex);
		Rank[] subRankArray = ArrayUtils.subarray(Rank.values(), deltaRank < 0 ? fromRankIndex + 1 : toRankIndex + 1, deltaRank < 0 ? toRankIndex : fromRankIndex);
		if (diagonal) {
			for (int i = 0; i < subFileArray.length; i++) {
				File file = subFileArray[i];
				Rank rank = subRankArray[i];
				if (board.getSquare(file, rank) != null) {
					return true;
				}
			}
		} else if (vertical) {
			for (File file : subFileArray) {
				if (board.getSquare(file, movement.getFromRank()) != null) {
					return true;
				}
			}
		} else {
			for (Rank rank : subRankArray) {
				if (board.getSquare(movement.getFromFile(), rank) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean isCheckAfterMovement(Board board, Movement movement) {
		Board boardCopy = new Board(board);
		boardCopy.addMovement(movement);
		Pair<File, Rank> kingPosition = boardCopy.getPosition(new ImmutablePair<Piece, Color>(Piece.KING, board.getTurn()));
		return boardCopy.isThreatened(kingPosition.getLeft(), kingPosition.getRight());
	}
}
