public class Link {
	Position position;
	String link;
	String linkText;
	
	public Link(Position pos_, String link_, String linkText_) {
		position = pos_;
		link = link_;
		linkText = linkText_;
	}
	
	public Link(Position pos_, String link_) {
		position = pos_;
		link = link_;
		linkText = link_;
	}
	
	public void setLinkText(String text) {
		linkText = text;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public String getDestination() {
		return link;
	}
	
	public String getLinkText() {
		return linkText;
	}
}
