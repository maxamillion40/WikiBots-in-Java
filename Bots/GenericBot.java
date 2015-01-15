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
	static ArrayList<String> MagicWords = new ArrayList<String>();
	static String articleName = "";
	static final int maxI = Integer.MAX_VALUE;
	
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
		String test = "Category:Cats";
		webpage = getWikiPage("User:ErnieParke/TestWikiBots");
		System.out.println(test.substring(1, test.length()-1));
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
		
		printLog();
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
		articleName = newPage.getTitle();
		
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
		Template temp;
		ArrayList<String> lines = page.getContent();
		Position pos;
		int k;
		for (int i = 0; lines.size()>i; i++) {
			for(int p = (lines.get(i)).indexOf("{{"); p != -1; p=(lines.get(i)).indexOf("{{", p+1)){
				pos = new Position(i, p);
				if (lines.get(i).indexOf("{{", p) != -1) {
					if (lines.get(i).indexOf("}}", p) != -1) {
						//We have a single line template.
						temp = parseTemplate(lines.get(i), p, pos);
						if (temp != null) {
							page.addTemplate(temp);
						}
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
		//Notification: MUST PARSE FOR TEMPLATE PARAMETERS
		Template temp;
		if (text.get(0).indexOf("|", buffer) != -1) {
			temp = new Template(pos, (text.get(0).substring(text.get(0).indexOf("{{", buffer) + 2, text.get(0).indexOf("|", buffer))));
		} else {
			temp = new Template(pos, (text.get(0)).substring(text.get(0).indexOf("{{", buffer) + 2));
		}
		parseTemplateTextForLinks(temp, text, buffer, pos);
		return temp;
	}
	
	static Template parseTemplate(String text, int buffer, Position pos) {
		//Parse a single line for a single template.
		String title;
		Template temp;
		if (text.indexOf("|", buffer) != -1) {
			title = text.substring(text.indexOf("{{", buffer) + 2, text.indexOf("|", buffer));
			temp = new Template(pos, title);
			parseLineForLinksImagesCategories(null, temp, text, buffer, text.indexOf("}}", buffer), pos, false);
		} else {
			title = text.substring(text.indexOf("{{", buffer) + 2, text.indexOf("}}", buffer));
			if (MagicWords.contains(title)) {
				return null;
			} else {
				temp = new Template(pos, title);
				parseLineForLinksImagesCategories(null, temp, text, buffer, text.indexOf("}}", buffer), pos, false);
			}
		}
		return temp;
	}
	
	static void parseTemplateTextForLinks(Template temp, ArrayList<String> lines, int buffer, Position pos) {
		//Parse multiple lines for links.
		parseLineForLinksImagesCategories(null, temp, lines.get(0), buffer, maxI, pos, false);
		for (int i = 1; i < lines.size(); i++) {
			pos.increaseLine(1);
			if (i+1 < lines.size()) {
				parseLineForLinksImagesCategories(null, temp, lines.get(i), buffer, maxI, pos, false);
			} else {
				parseLineForLinksImagesCategories(null, temp, lines.get(i), buffer, (lines.get(i).indexOf("}}")), pos, false);
			}
		}
	}
	
	static void parseLineForLinksImagesCategories(Page page, Template templ, String line, int buffer, int topBuffer, Position pos, boolean pageNotTemp) {
		//Parse a single line for links.
		//ADD IN IMAGE AND CATEGORY PARSING
		int i = line.indexOf("[[", buffer);
		int j = -1;
		int k = line.indexOf("[", buffer);
		String text;
		while ((i != -1 || k != -1) && (i<topBuffer || k<topBuffer)) {
			if (i <= k && i != -1) {
				//We have a Wikilink, image, or category.
				j = line.indexOf("]]", i);
				if (i != -1 && j == -1) {
					log("ERROR: Unclosed or multi-line link/image/category detected at " + new Position(pos.getLine(), i) + ".");
					return;
				}
				
				if (line.indexOf("||", i) != -1 && line.indexOf("||", i) < j) {
					log("ERROR: Double pipes detected in link/image/category at " + new Position(pos.getLine(), i) + ".");
				}
				if (line.indexOf("|", i) != -1 && line.indexOf("|", i) <= j) {
					text = line.substring(i+2, line.indexOf("|", i));	
				} else {
					text = line.substring(i+2, j);
				}
				if (!(text.length() > 9 && text.substring(0,9).equals("Category:"))) {
					if (!(text.length() > 5 && text.substring(0,5).equals("File:"))) {
						//We have a link!
						Link link = parseLink(page, line, text, i, pos, pageNotTemp);
						if (link != null) {
							if (pageNotTemp) {
								page.addLink(link);
							} else {
								templ.addLink(link);
							}
						}
					} else {
						//We have an image!
						k = i;
						i = line.indexOf("[[", i+1);
						j = line.indexOf("]]", k+1);
						if (i > j || i == -1) {
							i = k;
						} else {
							i = findClosingIndex(page, "[[", "]]", new Position(pos.getLine(), k));
						}
					}
				} else {
					//We have a category!
				}
				//Iteration!
				k = i;	
				i = line.indexOf("[[", k+1);
				k = line.indexOf("[", k+1);
			} else {
				//We might have an external link. Must check.
				Link link = parseExternalLink(page, line, k, j, pos);
				if (link != null) {
					if (pageNotTemp) {
						page.addLink(link);
					} else {
						templ.addLink(link);
					}
				}
				
				//Iteration!
				if (i != -1) {
					i = line.indexOf("[[", k+1);
				}
				k = line.indexOf("[", k+1);

			}
		}
	}
	
	static public Link parseLink(Page page, String line, String text, int i, Position pos, boolean PageNotTemp) {
		//We know we don't have a file or a category. Now to check we don't have an interwiki link.
		String linkText = null;
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
			if (text.substring(0,2).equals("/")) {
				//This link is headed to a subpage and the destination must reflect that.
				text = articleName + text;
				if (text.substring(text.length()-1).equals("/")) {
					linkText = text.substring(1, text.length()-1);
				}
			} else if (text.substring(0,2).equals(":")) {
				//Category and or file link.
				text = text.substring(1);
			}
			if (line.indexOf("|", i) < line.indexOf("]]", i) && line.indexOf("|", i) != -1) {
				//Parse for link text, the text a user actually sees.
				//Account for [[Scratch Wiki talk:Community Portal|]] = Community Portal
				linkText = line.substring(line.indexOf("|", i)+1, line.indexOf("]]", i));
				if (linkText.equals("")) {
					if (text.indexOf(":") == -1) {
						log("ERROR: Link with no displayed text detected at " + new Position(pos.getLine(), i) + ".");
					} else {
						linkText = text.substring(text.indexOf(":"));
					}
				}
			}
			Link link_;
			if (PageNotTemp) {
				if (linkText == null) {
					link_ = new Link(new Position(pos.getLine(), i), text);
				} else {
					link_ = new Link(new Position(pos.getLine(), i), text, linkText);
				}
				if (page.templatesContainLink(link_)) {
					return null;
				}
			} else {
				if (linkText == null) {
					link_ = new Link(new Position(pos.getLine(), i), text);
				} else {
					link_ = new Link(new Position(pos.getLine(), i), text, linkText);
				}
			}
			return link_;
		}
		return null;
	}
	
	static public Link parseExternalLink(Page page, String line, int k, int j, Position pos) {
		//We have an external link!
		String text;
		j = line.indexOf("]", k);
		if (line.indexOf("|", k) != -1 && line.indexOf("|", k) <= j) {
			text = line.substring(k+1, line.indexOf("|", k));
		} else {
			text = line.substring(k+1, j);
		}
		
		if (text.length() > 8) {
			if (text.substring(0, 7).equals("http://")) {
				Link link_ = new Link(new Position(pos.getLine(), k), text);
				if (page.templatesContainLink(link_)) {
					return link_;
				}
			}
		}
		return null;
	}
	
	static public void parsePageForLinks(Page page) {
		//Position, Link, Link Text
		ArrayList<String> content = page.getContent();
		for (int i = 0; i < content.size(); i++) {
			parseLineForLinksImagesCategories(page, null, content.get(i), 0, maxI, new Position(i, 0), true);
		}
	}
	
	static public void parsePageForCategories(Page page) {
		//Position, Title, Parameters
		
	}
	
	static public int findClosingIndex(Page page, String open, String close, Position start) {
		//Method for finding where [[ ]] and {{ }} end.
		int m = 1;
		int i = start.getPosInLine();
		int j;
		int k = 0;

		String line;
		for (int l = start.getLine(); m>0; l++) {
			//Looking one line at a time.
			line = page.getContentLine(l);
			k = i;
			i = line.indexOf(open, i+1);
			j = line.indexOf(close, k+1);
			if (i > j || i == -1) {
				i = k;
			} else {
				System.out.println(start);
				do {
					//Checking individual line.
					if (i<j && i != -1) {
						if (i != -1) {
							System.out.println(i + ":" + j);
							k = i;
							i = line.indexOf(open, i+1);
							j = line.indexOf(close, k+1);
							System.out.println(i + ":" + j);
						}
						m++;
					} else {
						if (i != -1) {
							i = line.indexOf(open, j+1);
						}
						j = line.indexOf(close, j+1);
						m--;
						if (m != 0) {
							k = j;
						}
					}
				} while (m != 0);
			}
			l++;
			if (m != 0) {
				i = 0;
				j = 0;
			}
		}
		return k;
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
	
	public static void printLog() {
		System.out.println("Log:");
		for (int i = 0; i < log.size(); i++) {
			System.out.println(log.get(i));
		}
	}
}
