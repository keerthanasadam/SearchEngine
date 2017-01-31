import java.io.BufferedReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Queue;
public class crawlerbfs {
	public static DB db = new DB();
	private static File path = new File("C:/JavaPrograms/weblinks");
	static Queue<String> queue = new LinkedList<String>();
	static LinkedHashSet<String> marked = new LinkedHashSet<String>();
	private static final int NUMBER_OF_LINKS = 3000;

	public static void downloadHTMLPage(String startURL, File path) {
		try {
			URL url = new URL(startURL);
			FileWriter wf = new FileWriter(path, true);
			URLConnection urlConnection = url.openConnection();
			urlConnection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			HttpURLConnection connection = null;
			if (urlConnection instanceof HttpURLConnection)
				connection = (HttpURLConnection) urlConnection;
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String current;
			while ((current = in.readLine()) != null) {
				wf.write(current);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static void deleteFiles(File file_to_delete) {
		try {
			if (file_to_delete.isDirectory()) {
				for (File f : file_to_delete.listFiles()) {
					if (f.isDirectory()) {
						deleteFiles(f);
					}
					f.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static void main(String[] args) throws SQLException, IOException {
		try {
			if (path.exists()) {
				deleteFiles(path);
			} else {
				path.mkdir();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		db.runSql2("TRUNCATE Record;");
		// initial web page
		String s = "http://www.unt.edu/";
		String sql = "select * from Record where URL = '"+s+"'";
		ResultSet rs = db.runSql(sql);
		if(rs.next()){
 
		}
		else
		{
		// list of web pages to be examined
		queue.add(s);

		// set of examined web pages
		marked.add(s);
		Document doc;
		System.out.println("List of url");
		// breadth first search crawl of web
		OUTER: while (!queue.isEmpty()) {

			String v = queue.remove();
			System.out.println(v);

			if (marked.size() < NUMBER_OF_LINKS) {
				try {
					doc = Jsoup.connect(v).get();
					Elements questions = doc.select("a[href]");
					for (Element link : questions) {
						if ((link.attr("abs:href").contains("unt.edu") && (link
								.attr("abs:href").startsWith("http")))) {
							if (marked.size() == NUMBER_OF_LINKS)
								continue OUTER;
							else {
								queue.add(link.attr("abs:href"));
								marked.add(link.attr("abs:href"));
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		System.out.println("Total Links downloaded :: " + marked.size());
		System.out.println(marked);
		int counter = 1;
		int listSize = marked.size();
		for (String fileName : marked) {
			System.out.println("Downloading .... file " + counter + "/"
					+ listSize + ".." + fileName);
			downloadHTMLPage(fileName, new File(path + "\\code" + counter
					+ ".html"));
			String htmlpage="code"+counter+".html";
		 sql = "INSERT INTO  `Crawler`.`Record` " + "(`RecordID`,`URL`) VALUES " + "(?,?);";
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, htmlpage);
			stmt.setString(2, fileName);
			stmt.execute();
			//db.runSql2(sql);
			counter++;
		}
		System.out.println("Finished downloads");
	}
}
}