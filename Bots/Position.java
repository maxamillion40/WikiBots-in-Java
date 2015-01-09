public class Position {
	int line;
	int posInLine;
	
	public Position(int line_, int posInLine_){
		line = line_;
		posInLine = posInLine_;
	}
	
	public void increaseLine(int i) {
		line += i;
	}

	public int getLine() {
		return line;
	}
	
	public int getPosInLine() {
		return getPosInLine();
	}
	
	@Override
	public String toString() {
		return "line " + line + " and position at letter " + posInLine;
	}
}
