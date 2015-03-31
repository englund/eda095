import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class ThreadedDownload extends Thread  {
	File file;
	URL url;
	
	ThreadedDownload(File dir, String link) throws MalformedURLException {
		file = new File(dir, parseFilename(link));
		url = new URL(link);
	}
	
	public void run() {
        System.out.println("Downloading " + url + " to " + file);
		try {
            BufferedInputStream in;
			in = new BufferedInputStream(url.openStream());

            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int offset = 0;
            while ((offset = in.read(buffer, 0, buffer.length)) > 0) {
            	out.write(buffer, 0, offset);
            }
            out.flush();
            out.close();
            in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
    private String parseFilename(String link) {
        return link.substring(link.lastIndexOf('/') + 1, link.length());
    }
}
