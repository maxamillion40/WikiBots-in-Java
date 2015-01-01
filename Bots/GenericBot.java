/**
 *Generic Bot code will go below. Generic Bot is the bot every other bot will be based off of.
 */
 
import java.net.*;
import java.io.*;
import java.util.ArrayList; 
import java.awt.Color;
import java.awt.Graphics;

public class GenericBot extends java.applet.Applet {
	
	/**
	 * Generic Bot Code Starts Below
	 * Please note that all bot classes will be children of this class.
	 */
	
	private static final long serialVersionUID = 1L;
	static Page webpage;
	
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
		//Call main method here in children.
		main(null);
	}
	
	static public void main(String[] args) {
		//This is where code will be put for children. Clear (but don't delete) once class completed.
		webpage = getWikiPage("Show (block)");
		for (int i = 0; i < webpage.getLineCount(); i++) {
			System.out.println(webpage.getContentLine(i));
		}
		System.out.println("Templates:");
		ArrayList<String> temp = webpage.getTemplates();
		for (int i = 0; i < temp.size(); i ++) {
			System.out.println(temp.get(i));
		}
	}
	
	static public Page getWikiPage(String name) {
		//This method fetches a Wiki page.
		String[] page;
		try{ 
			page = getURL("http://wiki.scratch.mit.edu/w/api.php?format=json&action=query&titles=" + URLEncoder.encode(name, "UTF-8") + "&prop=revisions&rvprop=content");
		} catch (IOException e) {
			return null;
		}
		return parseWikiPage(page[0]);
	}
	
	static public Page parseWikiPage(String XMLcode) {
		/**
		 * This is a custom built XML parser for Wiki pages.
		 * It extracts the title, pageID, page content, and templates used.
		 * Note to self: Please test on multiple pages to make sure it works flawlessly.
		 * Also, if needed, add support for detection of other article features, like links.
		 **/
		Page newPage = new Page();
		newPage.setTitle(parseXMLforInfo("title", XMLcode, 3, 1));
		newPage.setPageID(Integer.parseInt(parseXMLforInfo("pageid", XMLcode, 2, 0)));
		
		String line = "";
		int j = 0;
		for (int i = XMLcode.indexOf("wikitext") + 13; i!=-1; i=j) {
			j = XMLcode.indexOf("\\n", i+1);
			if (j > 0) {
				line = XMLcode.substring(i+2, j);
				newPage.addLine(line);
				if (line.length() > 2 && line.substring(0, 2).equalsIgnoreCase("{{")) {
					if (line.indexOf("|") == -1) {
						if (line.indexOf("}}") == -1) {
							newPage.addTemplate(line.substring(2));
						} else {
							newPage.addTemplate(line.substring(2, line.indexOf("}}")).trim());
						}
					} else {
						newPage.addTemplate(line.substring(2, line.indexOf("|")).trim());
					}
				}
			}
		}
		return newPage;
	}
	
	static public String parseXMLforInfo (String info, String XMLcode, int bufferBot, int bufferTop) {
		//This method aids in XML parsing.
		int i = 0;
		i = XMLcode.indexOf(info);
		i += info.length() + bufferBot;
		return XMLcode.substring(i, XMLcode.indexOf(",", i) - bufferTop);
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
