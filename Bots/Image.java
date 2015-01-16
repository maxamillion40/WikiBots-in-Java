import java.util.ArrayList;

public class Image {
	Position position;
	String name;
	ArrayList<String> parameters;
	ArrayList<Link> links = null;
	
	public Image(Position pos_, String name_, ArrayList<String> params, ArrayList<Link> links_) {
		position = pos_;
		name = name_;
		parameters = params;
		links = links_;
	}
	
	public Image(Position pos_, String name_) {
		position = pos_;
		name = name_;
		links = null;
	}
	
	public void addParameter(String param) {
		parameters.add(param);
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
	
	public int getParameterCount() {
		return parameters.size();
	}
	
	public String getParameter(int i) {
		return parameters.get(i);
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

		output = "(Image) Name: " + name + "\nWith links: ";
		for (int i = 0; i < links.size(); i++) {
			output += (links.get(i) + " , ");
		}
		output += "\nAt: " + position;
		return output;
	}
}
