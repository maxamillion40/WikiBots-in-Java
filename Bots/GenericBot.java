/**
 * Generic Bot is the parent of every other bot.
 */
 
import java.net.*;
import java.io.*;
import java.util.ArrayList; 
import java.util.Arrays;
import java.awt.Color;
import java.awt.Graphics;

public class GenericBot extends java.applet.Applet {
	
	/**
	 * Generic Bot Code Starts Below
	 * Please note that all bot classes will be children of this class.
	 */
	
	private static final long serialVersionUID = 1L;
	static Page webpage;
	static ArrayList<String> log = new ArrayList<String>();
	static ArrayList<String> Interwiki = new ArrayList<String>(Arrays.asList("de:", "id:", "ru:"));
	
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
		webpage = getWikiPage("User:ErnieParke/TestWikiBots");
		System.out.println("File:Cats".substring(0,5));
		System.out.println("Category:Cats".substring(0,9));
		for (int i = 0; i < webpage.getLineCount(); i++) {
			System.out.println(webpage.getContentLine(i));
		}

		ArrayList<Template> temp = webpage.getTemplates();
		ArrayList<Link> links;
		for (int i = 0; i < temp.size(); i ++) {
			System.out.println(temp.get(i));
		}
		
		links = webpage.getLinks();
		for (int i = 0; i < links.size(); i ++) {
			System.out.println(links.get(i));
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
		 * It extracts the title, pageID, page content, links used, templates used, and categories used.
		 * Note to self: Please test on multiple pages to make sure it works flawlessly.
		 **/
		Page newPage = new Page(parseXMLforInfo("title", XMLcode, 3, 1), Integer.parseInt(parseXMLforInfo("pageid", XMLcode, 2, 0)));
		
		String line = "";
		int j = 0;
		for (int i = XMLcode.indexOf("wikitext") + 13; i!=-1; i=j) {
			j = XMLcode.indexOf("\\n", i+1);
			if (j > 0) {
				line = XMLcode.substring(i+2, j);
				newPage.addLine(line);
			} else {
				line = XMLcode.substring(i+2, XMLcode.indexOf("\"}]"));
				newPage.addLine(line);
			}
		}
		parsePageForTemplates(newPage);
		parsePageForLinks(newPage);
		return newPage;
	}
	
	static public String parseXMLforInfo (String info, String XMLcode, int bufferBot, int bufferTop) {
		//This method aids in XML parsing.
		int i = 0;
		i = XMLcode.indexOf(info);
		i += info.length() + bufferBot;
		return XMLcode.substring(i, XMLcode.indexOf(",", i) - bufferTop);
	}
	
	static public void parsePageForTemplates(Page page) {
		//Just Title
		ArrayList<String> lines = page.getContent();
		Position pos;
		int k;
		for (int i = 0; lines.size()>i; i++) {
			for(int p = (lines.get(i)).indexOf("{{"); p != -1; p=(lines.get(i)).indexOf("{{", p+1)){
				pos = new Position(i, p);
				if (lines.get(i).indexOf("{{", p) != -1) {
					if (lines.get(i).indexOf("}}", p) != -1) {
						//We have a single line template.
						page.addTemplate(parseTemplate(lines.get(i), p, pos));
					} else {
						//We have a multi-line template.
						k = i;
						for (int j = -1; j == -1 || lines.size()<=i; i++) {
							j = lines.get(i).indexOf("}}", p);
						}
						if (lines.size()<i){
							log("ERROR: Unclosed template detected at line " + k + ".");
						} else {
							page.addTemplate(parseTemplate(new ArrayList<String>(lines.subList(k, i)), p, pos));
						}
					}
				}
			}
		}
	}
	
	static Template parseTemplate(ArrayList<String> text, int buffer, Position pos) {
		//Parse multiple lines for a single template.
		Template temp;
		if (text.get(0).indexOf("|", buffer) != -1) {
			temp = new Template(pos, (text.get(0).substring(text.get(0).indexOf("{{", buffer) + 2, text.get(0).indexOf("|", buffer))));
		} else {
			temp = new Template(pos, (text.get(0)).substring(text.get(0).indexOf("{{", buffer) + 2));
		}
		temp.setLinks(parseTextForLinks(text, buffer, pos));
		return temp;
	}
	
	static Template parseTemplate(String text, int buffer, Position pos) {
		//Parse a single line for a single template.
		String tempString;
		Template temp;
		if (text.indexOf("|", buffer) != -1) {
			tempString = text.substring(text.indexOf("{{", buffer) + 2, text.indexOf("|", buffer));
			temp = new Template(pos, tempString);
			temp.setLinks(parseLineForLinks(text, buffer, pos));
		} else {
			tempString = text.substring(text.indexOf("{{", buffer) + 2, text.indexOf("}}", buffer));
			temp = new Template(pos, tempString);
			temp.setLinks(parseLineForLinks(text, buffer, pos));
		}
		return temp;
	}
	
	static ArrayList<Link> parseTextForLinks(ArrayList<String> lines, int buffer, Position pos) {
		//Parse multiple lines for links.
		ArrayList<Link> tempLinks = new ArrayList<Link>();
		ArrayList<Link> tempLinks2 = new ArrayList<Link>();
		tempLinks2 = parseLineForLinks(lines.get(0), buffer, pos);
		for (int i = 1; i < lines.size(); i++) {
			pos.increaseLine(1);
			tempLinks2 = parseLineForLinks(lines.get(i), 0, pos);
			if (!tempLinks2.isEmpty()) {
				tempLinks.addAll(tempLinks2);
			}
		}
		return tempLinks;
	}
	
	static ArrayList<Link> parseLineForLinks(String line, int buffer, Position pos) {
		//Parse a single line for links.
		//EXPAND PARSING TO INCLUDE OUT OF WIKI LINKS, AND ALSO EXPAND WIKI LINKS TO INCLUDE ALTERNATE TEXTS
		ArrayList<Link> tempLinks = new ArrayList<Link>();
		int i = line.indexOf("[[", buffer);
		int j = -1;
		int k = line.indexOf("[", buffer);
		String text;
		while (i != -1 || k != -1) {
			if (i <= k && i != -1) {
				//We have a Wikilink, image, or category.
				j = line.indexOf("]]", i);
				if (i != -1 && j == -1) {
					log("ERROR: Unclosed or multi-line link/image/category detected at " + new Position(pos.getLine(), i));
					return null;
				}
				
				if (line.indexOf("|", i) != -1 && line.indexOf("|", i) <= j) {
					text = line.substring(i+2, line.indexOf("|", i));	
				} else {
					text = line.substring(i+2, j);
				}
				if (!(text.length() > 5 && text.substring(0,5).equals("File:")) && !(text.length() > 9 && text.substring(0,9).equals("Category:"))) {
					//We know we don't have a file or a category. Now to check we don't have an interwiki link.
					boolean temp = true;
					String temp2;
					for (int l = 0; l < Interwiki.size(); l++) {
						temp2 = Interwiki.get(l);
						if (text.length() > temp2.length() && text.substring(0,temp2.length()).equals(temp2)) {
							//We weed out interwiki links, checking against interwiki prefixes one at a time.
							temp = false;
						}
					}
					if (temp) {
						tempLinks.add(new Link(new Position(pos.getLine(), i), text));
					}
				}
				//Iteration!
				k = i;	
				i = line.indexOf("[[", i+1);
				k = line.indexOf("[", k + 1);
			} else {
				//We might have an external link. Must check.
				j = line.indexOf("]", k);
				if (line.indexOf("|", k) != -1 && line.indexOf("|", k) <= j) {
					text = line.substring(k+1, line.indexOf("|", k));
				} else {
					text = line.substring(k+1, j);
				}
				
				if (text.length() > 8) {
					if (text.substring(0, 7).equals("http://")) {
						tempLinks.add(new Link(new Position(pos.getLine(), k), text));
					}
				}
				//Iteration!
				if (i != -1) {
					i = line.indexOf("[[", k+1);
				}
				k = line.indexOf("[", k+1);

			}
		}
		return tempLinks;
	}
	
	static public void parsePageForLinks(Page page) {
		//Position, Link, Link Text
		ArrayList<String> content = page.getContent();
		ArrayList<Template> templates = page.getTemplates();
		ArrayList<Link> links = new ArrayList<Link>();
		ArrayList<Link> tempLinks;
		ArrayList<Link> tempLinks2;
		for (int i = 0; i < content.size(); i++) {
			tempLinks = parseLineForLinks(content.get(i), 0, new Position(i, 0));
			for (int j = 0; j < templates.size(); j++) {
				tempLinks2 = (templates.get(j)).getLinks();
				for (int k = 0; k < tempLinks2.size(); k++) {
					if (tempLinks.contains(tempLinks2.get(k))) {
						tempLinks.remove(tempLinks2.get(k));
					}
				}
			}
			if (!tempLinks.isEmpty()) {
				links.addAll(tempLinks);
			}
		}
		page.setLinks(links);
	}
	
	static public void parsePageForCategories(Page page) {
		//Position, Title, Parameters
		
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
	
	public static void log(String line) {
		log.add(line);
	}
	
	public void printLog() {
		for (int i = 1; i < log.size(); i++) {
			System.out.println(log.get(i));
		}
	}
}
