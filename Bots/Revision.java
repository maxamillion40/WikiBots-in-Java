import java.util.Date;

public class Revision {
	String title;
	String user;
	String comment;
	Date date;
	String page = null;
	
	public Revision(String title_, String user_, String comment_, Date date_, String page_) {
		title = title_;
		user = user_;
		comment = comment_;
		date = date_;
		page = page_;
	}
	
	public Revision(String title_, String user_, String comment_, Date date_) {
		title = title_;
		user = user_;
		comment = comment_;
		date = date_;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getPage() {
		return page;
	}
	
	@Override
	public String toString() {
		String output;

		output = "(Revision";
		if (page != null) {
			output += " ; Page included";
		} else {
			output += " ; Page not included";
		}
		output += ") Page: " + title + " User: " + user + " Timestamp: " + date + " Comment: " + comment;
		
		return output;
	}
}
