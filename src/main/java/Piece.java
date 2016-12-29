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
	
	public static Piece fromString(String str) {
		if ("K".equalsIgnoreCase(str)) {
			return KING;
		}
		if ("Q".equalsIgnoreCase(str)) {
			return QUEEN;
		}
		if ("R".equalsIgnoreCase(str)) {
			return ROOK;
		}
		if ("B".equalsIgnoreCase(str)) {
			return BISHOP;
		}
		if ("N".equalsIgnoreCase(str)) {
			return KNIGHT;
		}
		if ("".equalsIgnoreCase(str) || "P".equalsIgnoreCase(str)) {
			return PAWN;
		}
		return null;
	}
}
