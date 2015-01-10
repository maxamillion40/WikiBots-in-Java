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
		return posInLine;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(Position.class)) {
			Position pos2 = (Position)obj;
			if (pos2.getLine() == getLine() && pos2.getPosInLine() == getPosInLine()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "line " + line + " and letter " + posInLine;
	}
}
