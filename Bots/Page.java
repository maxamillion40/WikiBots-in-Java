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
	ArrayList<Link> links = new ArrayList<Link>();
	ArrayList<Template> templates = new ArrayList<Template>();
	ArrayList<Image> images = new ArrayList<Image>();
	ArrayList<String> categories = new ArrayList<String>();
	
	public Page(String title_, int pageID_) {
		title = title_;
		pageID = pageID_;
	}

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
	
	public void setTemplates(ArrayList<Template> templates_) {
		templates = templates_;
	}
	
	public void setImages(ArrayList<Image> images_) {
		images = images_;
	}
	
	public void addCategory(String category) {
		categories.add(category);
	}
	
	public void addLine(String content_) {
		content.add(content_);
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
	
	
	public String getTitle() {
		return title;
	}
	
	public int getPageID() {
		return pageID;
	}
	
	public ArrayList<String> getContent() {
		return content;
	}
	
	public String getContentLine(int lineID) {
		return content.get(lineID);
	}
	
	public ArrayList<Template> getTemplates() {
		return templates;
	}
	
	public ArrayList<Image> getImages() {
		return images;
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	
	public int getLineCount() {
		return content.size();
	}
	
	public boolean containsTemplate(String template) {
		return templates.contains(template);
	}
	
	public boolean templatesContainLink(Link link) {
		for (int i = 0; i < templates.size(); i ++) {
			if ((templates.get(i)).containsLink(link)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean imagesContainLink(Link link) {
		for (int i = 0; i < images.size(); i ++) {
			if ((images.get(i)).containsLink(link)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsCategory(String category) {
		for (int i = 0; i < categories.size(); i++) {
			if ((categories.get(i)).equals(category)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String output;

		output = "PAGE PAGE ;; Name: " + title + " ;; PAGE PAGE\nWith id: " + pageID  + "\n";
		for (int i = 0; i < content.size(); i++) {
			output += (content.get(i) + "\n");
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
			output += (categories.get(i) + ",");
		}
		return output;
	}
}
