import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler extends Thread {

	private WebCrawler webCrawler;

	public Crawler(WebCrawler webCrawler) {
		this.webCrawler = webCrawler;
	}

	public void run() {
		while (webCrawler.getIterations() < WebCrawler.MAX_ITERATIONS) {
			URL url = webCrawler.getNextUrl();
			if (webCrawler.isDying()){
				break;
			}
			Document doc = null;
			System.out.println("Thread: " + Thread.currentThread().getId() + " | On page: " + url);
			try {
				doc = Jsoup.parse(url, WebCrawler.TIMEOUT);
			} catch (IOException e) {
				System.out.println("Failed to load url: " + url);
				continue;
			}
			Elements links = doc.getElementsByTag("a");
			for (Element link : links) {
				String linkHref = link.attr("abs:href").trim();
				if (linkHref.isEmpty())
					continue;
				if (linkHref.startsWith("mailto:")) {
					webCrawler.addMail((linkHref.substring(linkHref.indexOf(':') + 1)));
				} else if (!linkHref.endsWith(".pdf")
						&& !webCrawler.historyContains(linkHref)) {
					webCrawler.addUrl(linkHref);
				}
			}
			webCrawler.increaseIterator();
		}
		System.out.println("Crawler Stopped");
	}
}
