/**
 * Generic Bot is the parent of every other bot.
 */
 
import java.net.*;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList; 
import java.util.Arrays;
import java.util.Locale;
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
	static ArrayList<String> NonTemplates = new ArrayList<String>();
	static final int maxI = Integer.MAX_VALUE;
	static final int revisionDepth = 10;
	static boolean getRevisionContent = true;
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
	
	public void init() {
		//This is where all initialization should occur.
		//We only create a window at the moment.
		//Look at method paint() for window code.
		setSize(400,400);
		setBackground(new Color(255,255,255));
		MagicWords.add("CURRENTDAY");
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
		//webpage = getWikiPage("User:ErnieParke/TestWikiBots");
		webpage = getWikiPage("Activity Feeds");
		System.out.println(webpage);

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
		parsePageForSections(newPage);
		getPastReveisions(newPage);
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
		Position end;
		for (int i = 0; lines.size()>i; i++) {
			for(int p = (lines.get(i)).indexOf("{{"); p != -1; p=(lines.get(i)).indexOf("{{", p+1)) {
				pos = new Position(i, p);
				if (lines.get(i).indexOf("{{", p) != -1) {
					end = findClosingPosition(page, "{{", "}}", new Position(i, lines.get(i).indexOf("{{", p)));
					if (end != null) {
						if (end.getLine() == i) {
							//We have a single line template.
							temp = parseTemplate(page, new ArrayList<String>(Arrays.asList(lines.get(i))), p, end.getPosInLine(), pos);
						} else {
							//We have a multi-line template.
							temp = parseTemplate(page, new ArrayList<String>(lines.subList(i, end.getLine()+1)), p, end.getPosInLine(), pos);
						}
						if (temp != null) {
							page.addTemplate(temp);
						}
					}
				}
			}
		}
	}
	
	static Template parseTemplate(Page page, ArrayList<String> text, int buffer, int topBuffer, Position pos) {
		//Parse multiple lines for a single template.
		//Notification: MUST PARSE FOR TEMPLATE PARAMETERS
		Template temp;
		String title;
		if (text.get(0).indexOf("|", buffer) != -1 && ((text.get(0).indexOf("|", buffer) < text.get(0).indexOf("}}", buffer)) || text.size() != 1)) {
			title = text.get(0).substring(text.get(0).indexOf("{{", buffer) + 2, text.get(0).indexOf("|", buffer));
			temp = new Template(pos, title);
		} else {
			if (text.get(0).indexOf("}}", buffer) != -1) {
				title = text.get(0).substring(text.get(0).indexOf("{{", buffer) + 2, text.get(0).indexOf("}}", buffer));
			} else {
				title = text.get(0).substring(text.get(0).indexOf("{{", buffer) + 2);
			}
			if (MagicWords.contains(title) || NonTemplates.contains(title)) {
				return null;
			} else {
				temp = new Template(pos, title);
			}
		}
		parseTemplateTextForLinks(page, temp, text, buffer, pos);
		parseTextForParameters(page, temp, null, text, buffer+2, topBuffer-1, pos, true);
		return temp;
	}
	
	static void parseTextForParameters(Page page, Template templ, Image img, ArrayList<String> text, int buffer, int topBuffer, Position pos, boolean TemplNotImg) {
		//We find parameters in templates.
		String line;
		int j = -1;
		int k = buffer;
		int q;
		String param = "";
		for (int i = 0; i < text.size(); i++) {
			//This for loop goes through a line at a time.
			line = text.get(i);
			if (i == 0) {
				j = line.indexOf("|", buffer);
			} else {
				j = line.indexOf("|");
			}
			for (int m = 0; j != -1 && !(i+1 == text.size() && j > topBuffer); m++) {
				if (m != 0) {
					k = j;
					j = line.indexOf("|", j+1);
				}
				
				//To ensure links don't mess up parameter parsing.
				q = line.indexOf("[[", k);
				while (q < j && q != -1) {
					if (q < j && q != -1) {
						q = findClosingPosition(page, "[[", "]]", new Position(pos.getLine() + i, q)).getPosInLine();
						j = line.indexOf("|", q);
					}
					q = line.indexOf("[[", q);
				}
				
				if (j == -1 && k != -1) {
					if (i + 1 == text.size()) {
						param = line.substring(k+1, topBuffer);
					} else {
						param = line.substring(k+1, line.length());
					}
				} else if (j != -1 && k != -1) {
					if (i + 1 == text.size() && j > topBuffer) {
						param = line.substring(k+1, topBuffer);
					} else {
						param = line.substring(k+1, j);
					}
				}
				if (m != 0) {
					if (TemplNotImg) {
						templ.addParameter(param);
					} else {
						img.addParameter(param);
					}
				}
			}
			j = -1;
			k = -1;
		}
	}
	
	static void parseTemplateTextForLinks(Page page, Template temp, ArrayList<String> lines, int buffer, Position pos) {
		//Parse multiple lines for links.
		for (int i = 0; i < lines.size(); i++) {
			if (i+1 < lines.size()) {
				parseLineForLinksImagesCategories(page, temp, null, lines.get(i), buffer, maxI, new Position(pos.getLine() + i, pos.getPosInLine()), 1);
			} else {
				parseLineForLinksImagesCategories(page, temp, null, lines.get(i), buffer, (lines.get(i).indexOf("}}")), new Position(pos.getLine() + i, pos.getPosInLine()), 1);
			}
		}
	}
	
	static void parseLineForLinksImagesCategories(Page page, Template templ, Image img, String line, int buffer, int topBuffer, Position pos, int inputDataType) {
		/*
		 * Variable Input Data Type:
		 * 0-Page (true)
		 * 1-Template (false)
		 * 2-Image
		 */
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
				if (line.indexOf("|", i) != -1 && line.indexOf("|", i) < j) {
					text = line.substring(i+2, line.indexOf("|", i));	
				} else {
					text = line.substring(i+2, j);
				}
				if ((text.length() > 9 && text.substring(0,9).equals("Category:"))) {
					//We have a category!
					page.addCategory(text.substring(9));
				} else if ((text.length() > 5 && text.substring(0,5).equals("File:"))) {
					//We have an image!
					k = i;
					i = line.indexOf("[[", i+1);
					j = findClosingPosition(page, "[[", "]]", new Position(pos.getLine(), k)).getPosInLine()-1;
					if (i > j || i == -1) {
						i = k;
					} else {
						i = j;
					}
					Image image = parseImage(page, line, text, i, pos, j, inputDataType == 0);
					if (image == null) {
						log("Image Error at: " + pos);
					} else {
						if (inputDataType == 0) {
							page.addImage(image);
						} else {
							templ.addImage(image);
						}
					}
				} else {
					//We have a link!
					Link link = parseLink(page, line, text, i, pos, inputDataType == 0);
					if (link != null) {
						if (inputDataType == 0) {
							page.addLink(link);
						} else {
							if (inputDataType == 1) {
								templ.addLink(link);
							} else {
								img.addLink(link);
							}
						}
					}
				}
				//Iteration!
				k = i;	
				i = line.indexOf("[[", k+1);
				k = line.indexOf("[", k+1);
			} else {
				//We might have an external link. Must check.
				Link link = parseExternalLink(page, line, k, j, pos);
				if (link != null) {
					if (inputDataType == 0) {
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
				text = page.getTitle() + text;
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
	
	static public Image parseImage(Page page, String line, String text, int i, Position pos, int topBuffer, boolean pageNotTemp) {
		//Position, name, parameters, links.
		Image image = new Image(pos, text);
		parseTextForParameters(page, null, image, new ArrayList<String>(Arrays.asList(line.substring(pos.getPosInLine()+1, topBuffer+1))), pos.getPosInLine(), topBuffer-1, pos, false);
		parseLineForLinksImagesCategories(page, null, image, line, pos.getPosInLine()+1, topBuffer+1, pos, 2);
		return image;
	}
	
	static public void parsePageForLinks(Page page) {
		//Position, Link, Link Text
		ArrayList<String> content = page.getContent();
		for (int i = 0; i < content.size(); i++) {
			parseLineForLinksImagesCategories(page, null, null, content.get(i), 0, maxI, new Position(i, 0), 0);
		}
	}
	
	static public Position findClosingPosition(Page page, String open, String close, Position start) {
		//Method for finding where [[ ]] and {{ }} end.
		int m = 1;
		int l = start.getLine();
		int i = start.getPosInLine();
		int j;
		int k = 0;

		String line = page.getContentLine(l);
		k = i;
		i = line.indexOf(open, i+1);
		j = line.indexOf(close, k+1);
		if (i > j || (i == -1 && j != -1)) {
			m = 0;
			k = j;
		} else {
			do {
				//Looking one line at a time.

				do {
					//Checking individual line.
					if (i<=j && i != -1) {
						if (i != -1) {
							k = i;
							i = line.indexOf(open, i+1);
							j = line.indexOf(close, k+1);
						}
						m++;
					} else if (j != -1) {
						if (i != -1) {
							i = line.indexOf(open, j+1);
						}
						k = j;
						j = line.indexOf(close, j+1);
						m--;
					}
				} while (m != 0 && j != -1);
				if (m != 0) {
					l++;
					if (l < page.getLineCount()) {
						line = page.getContentLine(l);
					}
					i = line.indexOf(open, 0);
					j = line.indexOf(close, 0);
					k = 0;
				}
			} while(m>0 && l < page.getLineCount());
		}
		if (l > page.getLineCount()) {
			log("ERROR: Unclosed parseable item at: " + start);
			return null;
		}
		return new Position(l, k+1);
	}
	
	public static void parsePageForSections(Page page) {
		//Position, title, depth.
		ArrayList<String> content = page.getContent();
		String line;
		boolean found;
		int index;
		int index2;
		Position pos;
		int j;
		for (int i = 0; i < content.size(); i++) {
			line = content.get(i);
			found = true;
			for (j = 1; found; j++) {
				found = false;
				index = line.indexOf(new String(new char[j]).replace("\0", "="));
				if (index == 0) {
					index2 = line.indexOf(new String(new char[j]).replace("\0", "="), index+1);
					if (index2 != -1) {
						found = true;
					}
				}
			}
			j -= 2;
			if (j != 0) {
				index = line.indexOf(new String(new char[j]).replace("\0", "="));
				index2 = line.indexOf(new String(new char[j]).replace("\0", "="), index+1);
				pos = new Position(i, 0);
				page.addSection(new Section(pos, line.substring(index+j, index2), j));
			}
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
	
	public static void getPastReveisions(Page page) {
		if (revisionDepth < 1) {
			return;
		}
		String[] returned;
		String XMLdata;
		String revision;
		String user;
		String comment;
		String tempDate;
		Date date = null;
		Page tempPage;
		int j = -1;
		int k = -1;
		try {
			if (getRevisionContent) {
				returned = getURL("http://wiki.scratch.mit.edu/w/api.php?format=xml&action=query&prop=revisions&pageids=" + page.getPageID() + "&rvprop=user|comment|timestamp|content&rvstartid=1000000000&rvendid=1&rvlimit=" + revisionDepth);
			} else {
				returned = getURL("http://wiki.scratch.mit.edu/w/api.php?format=xml&action=query&prop=revisions&pageids=" + page.getPageID() + "&rvprop=user|comment|timestamp&rvstartid=1000000000&rvendid=1&rvlimit=" + revisionDepth);
			}
		} catch (IOException e) {
			return;
		}
		XMLdata = compactArray(returned);
		for (int i = 0; i < revisionDepth; i++) {
			j = XMLdata.indexOf("<rev user=", k+1);
			k = XMLdata.indexOf("</rev>", j+1);
			revision = XMLdata.substring(j, k);
			user = parseXMLforInfo("user", revision);
			comment = parseXMLforInfo("comment", revision);
			tempDate = parseXMLforInfo("timestamp", revision);
			date = createDate(tempDate);
			page.addRevision(new Revision(user, comment, date, null));
		}
	}
	
	static public String parseXMLforInfo (String info, String XMLcode) {
		//This method aids in XML parsing.
		int i = XMLcode.indexOf(info);
		i += info.length() + 2;
		return XMLcode.substring(i, XMLcode.indexOf("\"", i+1) );
	}
	
	static public String compactArray(String[] array) {
		String output = "";
		
		for (String item: array) {
			output+=item;
		}
		
		return output;
	}
	
	public static Date createDate(String text) {
		Date date = null;
		try {
			date = dateFormat.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return date;
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
