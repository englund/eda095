import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Downloader {
    private URL url;
    private String html;

    public Downloader(URL url) throws IOException {
        this.url = url;
        this.html = downloadHtml();
    }

    public URL getUrl() {
        return url;
    }

    public String getHtml() {
        return html;
    }

    public ArrayList<String> getLinks() {
        ArrayList<String> links = new ArrayList<String>();
        Pattern urlPattern = Pattern.compile("href=\"(.*?)\"");
        Matcher match = urlPattern.matcher(getHtml());
        while (match.find()) {
            links.add(match.group(1));
        }
        return links;
    }

    public ArrayList<String> getPDFLinks() {
        ArrayList<String> pdfUrls = new ArrayList<String>();
        ArrayList<String> links = getLinks();
        for (String link : links) {
            if (link.endsWith(".pdf")) {
                pdfUrls.add(link);
            }
        }
        return pdfUrls;
    }

    public void downloadToPath(File dir, String link) throws IOException {
        File file = new File(dir, parseFilename(link));
        if(!file.exists()) {
            file.createNewFile();
            URL url = new URL(link);
            ReadableByteChannel channel = Channels.newChannel(url.openStream());
            FileOutputStream stream = new FileOutputStream(file);
            stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            stream.close();
        } else {
            throw new FileAlreadyExistsException(file.getPath());
        }
    }

    private String downloadHtml() throws IOException {
        InputStreamReader stream = new InputStreamReader(getUrl().openStream());
        BufferedReader in = new BufferedReader(stream);
        String line;
        while ((line = in.readLine()) != null) {
            html += line + "\n";
        }
        in.close();
        return html;
    }

    private String parseFilename(String link) {
        return link.substring(link.lastIndexOf('/') + 1, link.length());
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Too few parameters. Need path and URL.");
            System.exit(1);
        }

        File path = new File(System.getProperty("user.dir"), args[0]);
        if (!path.exists()) {
            System.out.println("Creating directory " + path);
            if (!path.mkdirs()) {
                System.out.println("Could not create directory " + path);
            }
        }
        String websiteUrl = args[1];

        URL url = null;
        try{
            url = new URL(websiteUrl);
        } catch (MalformedURLException e) {
            System.out.println("Url is malformed: " + websiteUrl);
            System.exit(1);
        }

        Downloader downloader = null;
        try {
            downloader = new Downloader(url);
        }  catch (IOException e) {
            System.out.println("Could not download HTML from " + url);
            System.exit(1);
        }

        ArrayList<String> pdfLinks = downloader.getPDFLinks();
        for (String pdfLink : pdfLinks) {
            try {
                System.out.println("Downloading " + pdfLink + " to " + path);
                downloader.downloadToPath(path, pdfLink);
            } catch (FileAlreadyExistsException e) {
                System.out.println("File already exists " + e.getFile());
            } catch (IOException e) {
                System.out.println("Could not download " + pdfLink);
            }
        }
    }
}
