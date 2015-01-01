import java.util.ArrayList;

public class Page {
	/**
	 * Page is a custom class designed to store Wiki pages.
	 * It includes several functions to edit, replace, and view specific article contents.
	 */
	
	String title;
	int pageID;
	ArrayList<String> content = new ArrayList<String>();
	ArrayList<String> templates = new ArrayList<String>();
	
	public Page() {
		//Initialize the page.
	}
	
	public void setTitle(String title_) {
		title = title_;
	}
	
	public void setPageID(int PageID_) {
		pageID = PageID_;
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
	
	public void setTemplates(ArrayList<String> templates_) {
		templates = templates_;
	}
	
	public void addLine(String content_) {
		content.add(content_);
	}
	
	public void addTemplate(String template) {
		templates.add(template);
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
	
	public ArrayList<String> getTemplates() {
		return templates;
	}
	
	public int getLineCount() {
		return content.size();
	}
	
	public boolean containsTemplate(String template) {
		return templates.contains(template);
	}
}
