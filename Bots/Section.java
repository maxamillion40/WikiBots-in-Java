public class Section {
	Position position;
	String title;
	int depth;
	
	public Section(Position pos_, String title_, int depth_) {
		position = pos_;
		title = title_;
		depth = depth_;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getDepth() {
		return depth;
	}
	
	@Override
	public String toString() {
		return "(Section) Title: " + title + " Depth: " + depth + " (Position: " + position + ")";
	}
	
	public String toString2() {
		return "(Section) Title: " + title + " (Position: " + position + ")";
	}
}
