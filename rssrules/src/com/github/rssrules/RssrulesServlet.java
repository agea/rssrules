package com.github.rssrules;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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

@SuppressWarnings("serial")
public class RssrulesServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		ArrayList<Exception> errors = new ArrayList<Exception>();

		resp.setContentType("application/rss+xml");
		try {

			URL url = new URL(req.getParameter("u"));
			String logic = req.getParameter("l");
			String[] titleContains = req.getParameterValues("tc");
			String[] notTitleContains = req.getParameterValues("ntc");
			String[] titleStarts = req.getParameterValues("ts");
			String[] notTitleStarts = req.getParameterValues("nts");

			SyndFeedInput input = new SyndFeedInput();

			SyndFeed feed = input.build(new XmlReader(url));
			Iterator i = feed.getEntries().iterator();
			boolean rem = false;
			while (i.hasNext()) {
				i.next();
				rem = !rem;
				if (rem) {
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
