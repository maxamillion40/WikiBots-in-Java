import java.awt.Graphics;

public class WMmain extends GenericBot {

	private static final long serialVersionUID = 1L;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		main(null);
	}
	
	static public void main(String[] args) {
		Page page = getWikiPage("Show (block)");
		System.out.println(page);
	}
}
