import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class WebCrawler {
	public static final int TIMEOUT = 10000;
	public static final int MAX_ITERATIONS = 100;
	
	private HashSet<String> history;
	private HashSet<String> mailSet;
	private Queue<URL> queue;
	private int iterations = 0;
	private boolean die = false;
	
	public WebCrawler() {
		history = new HashSet<String>();
		mailSet = new HashSet<String>();
		queue = new LinkedList<URL>();
	}
	
	public static void main(String[] args) throws MalformedURLException, InterruptedException {
		WebCrawler webCrawler = new WebCrawler();
		
		webCrawler.addUrl("http://cs.lth.se/eda095/");
		
		ArrayList<Crawler> crawlers = new ArrayList<Crawler>();
		for (int i = 0; i < 5; i++) {
			Crawler crawler = new Crawler(webCrawler);
			crawlers.add(crawler);
			crawler.start();
		}
		
		for (Crawler c : crawlers) {
			c.join();
		}
		
		for (String mail : webCrawler.getMailSet()) {
			System.out.println(mail);
		}
	}
	
	public synchronized void addMail(String mail) {
		mailSet.add(mail);
	}
	
	public synchronized void addUrl(String link) {
		try {
			queue.add(new URL(link));
			history.add(link);
			notifyAll();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized URL getNextUrl() {
		while (queue.isEmpty() && !this.isDying()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return queue.poll();
	}
	
	private HashSet<String> getMailSet() {
		return mailSet;
	}
	
	public synchronized int getIterations(){
		return iterations;
	}
	
	public synchronized void increaseIterator() {
		iterations++;
	}
	
	public synchronized void die() {
		die  = true;
	}
	
	public synchronized boolean isDying(){
		return die;
	}
	
	public boolean historyContains(String linkHref) {
		return history.contains(linkHref);
	}
}
