package com.github.rssrules;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.XmlReader;

@SuppressWarnings("serial")
public class RssrulesServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("application/rss+xml");

		URL url = new URL(req.getParameter("url"));

		SyndFeedInput input = new SyndFeedInput();

		try {
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
			resp.getWriter().close();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
