public class Movement {
	private File fromFile, toFile;
	private Rank fromRank, toRank;
	
	public Movement(File fromFile, Rank fromRank, File toFile, Rank toRank) {
		this.fromFile = fromFile;
		this.fromRank = fromRank;
		this.toFile = toFile;
		this.toRank = toRank;
	}

	public File getFromFile() {
		return fromFile;
	}

	public Rank getFromRank() {
		return fromRank;
	}

	public File getToFile() {
		return toFile;
	}

	public Rank getToRank() {
		return toRank;
	}
}
