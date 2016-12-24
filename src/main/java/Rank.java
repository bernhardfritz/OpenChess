public enum Rank {
	ONE("1"),
	TWO("2"),
	THREE("3"),
	FOUR("4"),
	FIVE("4"),
	SIX("6"),
	SEVEN("7"),
	EIGHT("8"),
	;
	
	private String value;
	
	private Rank(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}