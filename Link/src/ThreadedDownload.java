import java.io.File;
import java.net.MalformedURLException;

public class ThreadedDownload extends Thread  {
	PDFDownloader pdfdownloader;
	
	ThreadedDownload(File dir, String link) throws MalformedURLException {
		pdfdownloader = new PDFDownloader(dir, link);
	}
	
	public void run() {
        System.out.println("Downloading " + pdfdownloader.getUrl() + " to " + pdfdownloader.getFile());
		pdfdownloader.download();
    }
}
