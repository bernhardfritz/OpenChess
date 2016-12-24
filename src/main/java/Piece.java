public enum Piece {
	KING("K"),
	QUEEN("Q"),
	ROOK("R"),
	BISHOP("B"),
	KNIGHT("N"),
	PAWN(""),
	;
	
	private String value;
	
	private Piece(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
