public enum Color {
	WHITE, BLACK;
	
	public Color getOpposite() {
		return WHITE == this ? BLACK : WHITE;
	}
}
