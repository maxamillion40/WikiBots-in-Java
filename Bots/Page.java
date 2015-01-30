import java.util.ArrayList;

public class Page {
	/**
	 * Page is a custom class designed to store Wiki pages.
	 * It includes several functions to edit, replace, and view specific article contents.
	 * 
	 * Look at this page for links: https://github.com/maxamillion40/WikiBots-in-Java/wiki
	 */
	
	String title;
	int pageID;
	ArrayList<String> content = new ArrayList<String>();
	ArrayList<Section> sections = new ArrayList<Section>();
	ArrayList<Link> links = new ArrayList<Link>();
	ArrayList<Template> templates = new ArrayList<Template>();
	ArrayList<Image> images = new ArrayList<Image>();
	ArrayList<String> categories = new ArrayList<String>();
	ArrayList<Revision> revisions = new ArrayList<Revision>();
	
	public Page(String title_, int pageID_) {
		title = title_;
		pageID = pageID_;
	}

	//Set variables.
	public void setContent(ArrayList<String> content_) {
		content = content_;
	}
	
	public void setContentLine(String content_, int lineID) {
		if (lineID >= content.size()) {
			return;
		} else {
			content.set(lineID, content_);
		}
	}
	
	//Modify variables.
	public void addLine(String content_) {
		content.add(content_);
	}
	
	public void addSection(Section section) {
		sections.add(section);
	}
	
	
	public void addLink(Link link) {
		links.add(link);
	}
	
	public void addTemplate(Template template) {
		templates.add(template);
	}
	
	public void addImage(Image image) {
		images.add(image);
	}
	
	public void addCategory(String category) {
		categories.add(category);
	}
	
	public void addRevisions(ArrayList<Revision> revisions_) {
		revisions.addAll(revisions_);
	}
	
	//Get information.
	//Title methods
	public String getTitle() {
		return title;
	}
	
	//Page id methods
	public int getPageID() {
		return pageID;
	}
	
	//Content methods
	public ArrayList<String> getContent() {
		return content;
	}
	
	public String getContentLine(int lineID) {
		return content.get(lineID);
	}
	
	public int getLineCount() {
		return content.size();
	}
	
	//Section methods
	public Section getSection(int i) {
		return sections.get(i);
	}
	
	public Section getSection(Position pos) {
		int i = 0;
		while (pos.isGreaterThen(sections.get(i).getPosition())) {
			i++;
		}
		if (i == 0) {
			return null;
		} else {
			return sections.get(i-1);
		}
	}
	
	//Link methods
	public ArrayList<Link> getLinks() {
		return links;
	}
	
	//Template methods
	public boolean templatesContainLink(Link link) {
		for (int i = 0; i < templates.size(); i ++) {
			if ((templates.get(i)).containsLink(link)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Template> getTemplates() {
		return templates;
	}
	
	public boolean containsTemplate(String template) {
		return templates.contains(template);
	}
	
	//Image methods
	public ArrayList<Image> getImages() {
		return images;
	}
	
	public boolean imagesContainLink(Link link) {
		for (int i = 0; i < images.size(); i ++) {
			if ((images.get(i)).containsLink(link)) {
				return true;
			}
		}
		return false;
	}
	
	//Category methods
	public boolean containsCategory(String category) {
		for (int i = 0; i < categories.size(); i++) {
			if ((categories.get(i)).equals(category)) {
				return true;
			}
		}
		return false;
	}
	
	//Revision methods
	public int getRevisionCount() {
		return revisions.size();
	}
	
	public Revision getRevision(int i) {
		return revisions.get(i);
	}
	
	@Override
	public String toString() {
		String output;

		output = "PAGE PAGE ;; Name: " + title + " ;; PAGE PAGE\nWith id: " + pageID  + "\n";
		for (int i = 0; i < content.size(); i++) {
			output += (content.get(i) + "\n");
		}
		output += "\nWith sections: \n";
		for (int i = 0; i < sections.size(); i++) {
			output += (sections.get(i).toString2() + "\n");
		}	
		output += "\nWith links: \n";
		for (int i = 0; i < links.size(); i++) {
			output += (links.get(i) + "\n");
		}
		output += "\nWith templates: \n";
		for (int i = 0; i < templates.size(); i++) {
			output += (templates.get(i) + "\n");
		}
		output += "\nWith images: \n";
		for (int i = 0; i < images.size(); i++) {
			output += (images.get(i) + "\n");
		}
		output += "\nWith categories: \n";
		for (int i = 0; i < categories.size(); i++) {
			output += (categories.get(i) + " , ");
		}
		output += "\n\nWith revision history: \n";
		for (int i = 0; i < revisions.size(); i++) {
			output += (revisions.get(i) + "\n");
		}
		return output;
	}
}
