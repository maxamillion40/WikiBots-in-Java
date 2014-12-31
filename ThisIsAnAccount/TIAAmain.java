import java.net.*;
import java.io.*;
import java.util.ArrayList; 
import java.awt.Color;
import java.awt.Graphics;

public class TIAAmain extends java.applet.Applet {
	
	/**
	 * This Is An Account Code Starts Below
	 */
	
	private static final long serialVersionUID = 1L;
	static String[] webpage;
	
	public void init() {
		//This is where all initialization should occur.
		//We only create a window at the moment.
		//Look at method paint() for window code.
		setSize(400,400);
		setBackground(new Color(255,255,255));
	}
	
	public void paint(Graphics g) {
		//Window code.
		g.setColor(Color.BLACK);
		g.drawString("Applet background", 0, 50);
		main(null);
	}
	
	static public void main(String[] args) {
		//This is where we start our actual code! :D
		webpage = getWikiPage("Show (block)");
		for (String st : webpage) {
			System.out.println(st);
		}
	}
	
	static public String[] getWikiPage(String name) {
		//This method fetches a Wiki page.
		try{ 
			return getURL("http://wiki.scratch.mit.edu/wiki/" + name);
		} catch (IOException e) {
			return null;
		}
	}
	
	static public String[] getURL(String ur) throws IOException {
		//This method actual fetches a web page, and turns it into a more easily use-able format.
        URL oracle = null;
		try {
			oracle = new URL(ur);
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
		}
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));

        ArrayList<String> page = new ArrayList<String>();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            page.add(inputLine);
        }
        in.close();
        return page.toArray(new String[page.size()]);
	}
}
