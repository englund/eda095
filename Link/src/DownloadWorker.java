import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentLinkedQueue;


public class DownloadWorker extends Thread  {
	ConcurrentLinkedQueue<String> downloadQueue;
	File dir;
	
	DownloadWorker(File dir, ConcurrentLinkedQueue<String> downloadQueue) throws MalformedURLException {
		this.downloadQueue = downloadQueue;
		this.dir = dir;
	}
	
	public void run() {
		String link;
		while ((link = downloadQueue.poll()) != null){
			try {
				PDFDownloader pdfdownloader = new PDFDownloader(dir, link);
				System.out.println("START: " + pdfdownloader.getUrl() + " @ " + pdfdownloader.getFile());
				pdfdownloader.download();
				System.out.println("DONE: " + pdfdownloader.getUrl());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
    }
}