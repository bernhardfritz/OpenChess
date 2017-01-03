import static org.junit.Assert.*;

import org.junit.Test;

public class Knight {
	
	public Board board = Board.create();

	public Movement getMov(String movement){
		String[] numbers={"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN"};
		String[] parts = movement.split("");
		Movement m = new Movement(File.valueOf(parts[0].toUpperCase()),
				Rank.valueOf(numbers[Integer.parseInt(parts[1])-1]),
				File.valueOf(parts[3].toUpperCase()),
				Rank.valueOf(numbers[Integer.parseInt(parts[4])-1]),
				board.getToken());
		return m;
	}
	public void movement(String movement, boolean b){
		Movement m = getMov(movement);
		if(b){
			assertTrue(Rules.isLegitMovement(board, m));
		}
		else if(!b){
			assertFalse(Rules.isLegitMovement(board, m));

		}
	}
	public void trueMov(String m){
		movement(m, true);
	}	
	public void falseMov(String m){
		movement(m, false);
	}
	
	public void move(String movement){
		Movement m = getMov(movement);
		board.addMovement(m);
	}

	@Test
	public void staring_position_possible_movements() {
		trueMov("b1_a3");
		trueMov("b1_c3");
		trueMov("g1_f3");
		trueMov("g1_h3");
	}

	@Test
	public void staring_position_illegal_movements() {
		falseMov("b1_a1");
		falseMov("b1_c1");
		falseMov("b1_a2");
		falseMov("b1_c2");
	}

	@Test
	public void staring_position_unit_already_there_movements() {
		falseMov("b1_d2");
		falseMov("g1_e2");
	}
	
	@Test
	public void center_valid_movements() {
		trueMov("b1_c3");
		move("b1_c3");
		
		trueMov("h7_h6");//black move
		move("h7_h6");
		
		trueMov("c3_e4");
		move("c3_e4");
		
		trueMov("g7_g6");//black move
		move("g7_g6");
		
		//up
		trueMov("e4_c5");
		trueMov("e4_d6");
		trueMov("e4_f6");
		trueMov("e4_g5");
		
		move("e4_c5");
		
		trueMov("f7_f6");//black move
		move("f7_f6");
		
		//down
		trueMov("c5_a4");
		trueMov("c5_b3");
		trueMov("c5_d3");
		trueMov("c5_e4");
	}
	
	@Test
	public void center_invalid_movements() {
		trueMov("b1_c3");
		move("b1_c3");
		
		trueMov("h7_h6");//black move
		move("h7_h6");
		
		trueMov("c3_e4");
		move("c3_e4");
		
		trueMov("g7_g6");//black move
		move("g7_g6");
		
		//up
		falseMov("e4_d3");
		falseMov("e4_d4");
		falseMov("e4_d5");
		falseMov("e4_e5");
		falseMov("e4_f5");
		falseMov("e4_f4");
		falseMov("e4_e3");
	}
	
	@Test
	public void capture_check() {
		board = Board.create();
		
		trueMov("b1_c3");
		move("b1_c3");
		
		trueMov("h7_h6");//black move
		move("h7_h6");
		
		trueMov("c3_d5");
		move("c3_d5");
		
		trueMov("g7_g6");//black move
		move("g7_g6");
		
		//capturing pawns
		trueMov("d5_c7");
		trueMov("d5_e7");
		
		move("d5_c7");	//take left pawn
		
		falseMov("f7_f6");//black move
		//black has to react to check

		trueMov("d8_c7");//queen takes
	}
	
	public void capture_retreat() {
		board = Board.create();
		
		trueMov("b1_c3");
		move("b1_c3");
		
		trueMov("h7_h6");//black move
		move("h7_h6");
		
		trueMov("c3_d5");
		move("c3_d5");
		
		trueMov("g7_g6");//black move
		move("g7_g6");
		
		//capturing pawns
		trueMov("d5_c7");
		trueMov("d5_e7");
		
		move("d5_e7");	//take left pawn
		
		trueMov("f7_f6");//black move
		move("f7_f6");
		
		//retreat
		trueMov("e7_d5");
		move("e7_d5");
		
		//e7 is dead
		falseMov("e7_e6");//black move
	}
}
