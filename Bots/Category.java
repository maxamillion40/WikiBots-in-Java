import java.util.ArrayList;

public class Category {
	Position position;
	ArrayList<String> categories;
	
	public Category(Position pos_, ArrayList<String> categories_) {
		position = pos_;
		categories = categories_;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public boolean containsCategory(String category) {
		return categories.contains(category);
	}
}
