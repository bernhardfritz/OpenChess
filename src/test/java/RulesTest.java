import org.junit.Test;

import static org.junit.Assert.*;

public class RulesTest {
	@Test
	public void pawnMovement() {
		Board board = Board.create();
		Movement e2_e3 = new Movement(File.E, Rank.TWO, File.E, Rank.THREE);
		assertTrue(Rules.isLegitMovement(board, e2_e3)); // test movement of white pawn from e2 to e3
		Movement e2_e4 = new Movement(File.E, Rank.TWO, File.E, Rank.FOUR);
		assertTrue(Rules.isLegitMovement(board, e2_e4)); // test movement of white pawn from e2 to e4
		Movement e2_f3 = new Movement(File.E, Rank.TWO, File.F, Rank.THREE);
		assertFalse(Rules.isLegitMovement(board, e2_f3)); // test movement of white pawn from e2 to f3 (diagonal movement not allowed, only during capture)
		Movement d7_d5 = new Movement(File.D, Rank.SEVEN, File.D, Rank.FIVE);
		assertFalse(Rules.isLegitMovement(board, d7_d5)); // test movement of black pawn from d7 to d5 (black is not allowed to begin)
		board.addMovement(e2_e4);
		assertTrue(Rules.isLegitMovement(board, d7_d5)); // test movement of black pawn from d7 to d5 (black is allowed to move after white has moved)
		board.addMovement(d7_d5);
		Movement e4_e3 = new Movement(File.E, Rank.FOUR, File.E, Rank.THREE);
		assertFalse(Rules.isLegitMovement(board, e4_e3)); // test movement of white pawn from e4 to e3 (pawn is not allowed to move backwards)
		Movement e4_e6 = new Movement(File.E, Rank.FOUR, File.E, Rank.SIX);
		assertFalse(Rules.isLegitMovement(board, e4_e6)); // test movement of white pawn from e4 to e6 (pawn is not allowed to move 2 squares if it has already moved in the past)
		Movement e4xd5 = new Movement(File.E, Rank.FOUR, File.D, Rank.FIVE); 
		assertTrue(Rules.isLegitMovement(board, e4xd5)); // test capture of black pawn at d5 by white pawn from e4
		board.addMovement(e4xd5);
		Movement e7_e5 = new Movement(File.E, Rank.SEVEN, File.E, Rank.FIVE);
		assertTrue(Rules.isLegitMovement(board, e7_e5)); // test movement of black pawn from e7 to e5
		board.addMovement(e7_e5);
		Movement d5xe6 = new Movement(File.D, Rank.FIVE, File.E, Rank.SIX);
		assertTrue(Rules.isLegitMovement(board, d5xe6)); // test en passant capture of black pawn at e5 by white pawn from d5
	}
}
