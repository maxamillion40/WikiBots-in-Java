import java.util.Date;

public class Revision {
	String user;
	String comment;
	Date date;
	String page = null;
	
	public Revision(String user_, String comment_, Date date_, String page_) {
		user = user_;
		comment = comment_;
		date = date_;
		page = page_;
	}
	
	public Revision(String user_, String comment_, Date date_) {
		user = user_;
		comment = comment_;
		date = date_;
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
		}
		output += ") User: " + user + " Timestamp: " + date + " Comment: " + comment;
		
		return output;
	}
}
