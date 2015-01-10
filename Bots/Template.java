import java.util.ArrayList;

public class Template {
	Position position;
	String name;
	ArrayList<Link> links = null;
	
	public Template(Position pos_, String name_, ArrayList<Link> links_) {
		position = pos_;
		name = name_;
		links = links_;
	}
	
	public Template(Position pos_, String name_) {
		position = pos_;
		name = name_;
		links = null;
	}
	
	public void setLinks(ArrayList<Link> links_) {
		links = links_;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	
	public boolean containsLink(String link) {
		for (int i = 0; i < links.size(); i++) {
			if ((links.get(i)).getDestination() == link) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String output;

		output = "(Template) Name: " + name + "\nWith links: ";
		for (int i = 0; i < links.size(); i++) {
			output += (links.get(i) + " , ");
		}
		return output;
	}
}
