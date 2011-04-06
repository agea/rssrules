package com.github.rssrules;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

public class RssrulesServlet extends HttpServlet {

	private static final long serialVersionUID = -8848841096627270590L;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		ArrayList<Exception> errors = new ArrayList<Exception>();

		resp.setContentType("application/rss+xml");
		try {

			URL url = new URL(req.getParameter("u"));
			boolean and = req.getParameter("l").equals("and");

			SyndFeed feed = new SyndFeedInput().build(new XmlReader(url));
			Iterator i = feed.getEntries().iterator();

			while (i.hasNext()) {
				SyndEntry post = (SyndEntry) i.next();
				boolean remove = true;
				for (Entry<String, String[]> entry : (Set<Entry<String, String[]>>) req
						.getParameterMap().entrySet()) {
					String k = entry.getKey();
					if (k.equals("u") || k.equals("l")) {
						continue;
					}
					String[] vs = entry.getValue();
					Boolean breakFor = false;
					for (String v : vs) {
						boolean neg = k.startsWith("n");
						if (neg) {
							k = k.substring(1);
						}
						String[] tbv = new String[0];
						String part = k.substring(0, 1);
						if (part.equals("p")) {
							tbv = new String[] { post.getCategories()
									.toString()
									+ post.getTitle()
									+ post.getDescription() + post.getAuthor() };
						} else if (part.equals("t")) {
							tbv = new String[] { post.getTitle() };
						} else if (part.equals("c")) {
							Object[] obj = post.getCategories().toArray();
							tbv = new String[obj.length];
							for (int j = 0; j < obj.length; j++) {
								tbv[j] = obj[j].toString();
							}
						} else if (part.equals("a")) {
							tbv = new String[] { post.getAuthor() };
						} else if (part.equals("b")) {
							tbv = new String[] { post.getDescription()
									.getValue() };
						}
						Boolean m = false;
						String r = k.substring(1, 2);
						if (r.equals("c")) {
							for (String tbvs : tbv) {
								m = tbvs.toLowerCase()
										.contains(v.toLowerCase()) || m;
							}
						} else if (r.equals("s")) {
							for (String tbvs : tbv) {
								m = tbvs.toLowerCase().startsWith(
										v.toLowerCase())
										|| m;
							}
						} else if (r.equals("e")) {
							for (String tbvs : tbv) {
								m = tbvs.toLowerCase()
										.endsWith(v.toLowerCase()) || m;
							}
						} else if (r.equals("m")) {
							Pattern p = Pattern.compile(v);
							for (String tbvs : tbv) {
								m = p.matcher(tbvs).matches() || m;
							}
						} else {
							throw new Exception("Unknown rule: " + r
									+ ", in param: " + k);
						}

						if (m != neg) {
							if (!and) {
								i.remove();
								breakFor = true;
								break;
							} else {

							}
						} else {
							if (and) {
								remove = false;
								breakFor = true;
								break;
							}
						}

					}
					if (breakFor) {
						break;
					}
				}
				if (and && remove) {
					i.remove();
				}
			}

			SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
			syndFeedOutput.output(feed, resp.getWriter());
			return;
		} catch (Exception e) {
			errors.add(e);
		}
		SyndFeedImpl syndFeedImpl = new SyndFeedImpl();
		syndFeedImpl.setFeedType("rss_2.0");
		syndFeedImpl.setTitle("Something bad happended");
		syndFeedImpl.setDescription("Errors processing request");
		syndFeedImpl.setLink(req.getRequestURI());
		ArrayList<SyndEntry> entries = new ArrayList<SyndEntry>();
		for (Exception e : errors) {
			SyndEntryImpl entry = new SyndEntryImpl();
			SyndContentImpl desc = new SyndContentImpl();
			desc.setType("text/html");
			desc.setValue("<pre>" + this.getStackTrace(e) + "</pre>");
			entry.setTitle(e.getClass().getSimpleName());
			entry.setLink(req.getRequestURI());
			entry.setPublishedDate(new Date());
			entry.setDescription(desc);
			entries.add(entry);
		}
		syndFeedImpl.setEntries(entries);
		SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
		try {
			syndFeedOutput.output(syndFeedImpl, resp.getWriter());
		} catch (FeedException e1) {
			throw new RuntimeException(e1);
		}

	}

	private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
}
