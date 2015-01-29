import java.sql.Date;

public class Revision {
	String user;
	String comment;
	Date date;
	Page page = null;
	
	public Revision(String user_, String comment_, Date date_, Page page_) {
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
}
