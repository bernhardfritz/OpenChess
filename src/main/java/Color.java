public enum Color {
	WHITE, BLACK;
	
	public Color getOpposite() {
		return WHITE == this ? BLACK : WHITE;
	}
	
	public static Color fromString(String str) {
		if ("WHITE".equalsIgnoreCase(str) || "w".equalsIgnoreCase(str)) {
			return WHITE;
		}
		if ("BLACK".equalsIgnoreCase(str) || "b".equalsIgnoreCase(str)) {
			return BLACK;
		}
		return null;
	}
}
