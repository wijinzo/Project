package org.finalproject.book.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;


import org.finalproject.book.web.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleQuery {
	public String searchKeyword;
	public String url;
	public String content;
	public WebPage page;

	public GoogleQuery(String searchKeyword) {
		this.searchKeyword = searchKeyword;
		try {
			// This part has been specially handled for Chinese keyword processing.
			// You can comment out the following two lines
			// and use the line of code in the lower section.
			// Also, consider why the results might be incorrect
			// when entering Chinese keywords.
			String encodeKeyword = java.net.URLEncoder.encode(searchKeyword, "utf-8");
			String mainKeyword = "書店";
			String encodeMainKeyword = java.net.URLEncoder.encode(mainKeyword, "utf-8");

//			this.url = "https://www.google.com/search?q=" + encodeKeyword + "+" + encodeMainKeyword + "&oe=utf8&num=20";
			this.url = "https://www.google.com/search?q=" + encodeKeyword + "+" + encodeMainKeyword + "&oe=utf8&num=5";
			
			// this.url =
			// "https://www.google.com/search?q="+searchKeyword+"&oe=utf8&num=20";
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String fetchContent() throws IOException {
		StringBuilder retVal = new StringBuilder();
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection)u.openConnection();
		// set HTTP header
		conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");
		InputStream in = conn.getInputStream();

		InputStreamReader inReader = new InputStreamReader(in, "utf-8");
		BufferedReader bufReader = new BufferedReader(inReader);
		String line;

		while ((line = bufReader.readLine()) != null) {
			retVal.append(line);
		}
		return retVal.toString();
	}

	public List<SearchResult> query() throws IOException {
        if (content == null) {
            content = fetchContent();
        }

        List<SearchResult> results = new ArrayList<>();

		// using Jsoup analyze html string
		Document doc = Jsoup.parse(content);
		// select particular element(tag) which you want
		Elements lis = doc.select("div.kCrYT a"); // 更精確地選擇帶有 <a> 標籤的元素

		for (Element li : lis) {
			try {
				String citeUrl = li.attr("href").replace("/url?q=", "");
                String title = li.text();
                String siteName = li.select("cite").text();

				if (title.equals("")) {
					continue;
				}


				citeUrl = URLDecoder.decode(citeUrl, "UTF-8");// 原本的url不能訪問，要先解碼
				citeUrl = removeQueryParams(citeUrl);


				citeUrl = getRedirectedUrl(citeUrl);

				SearchResult result = new SearchResult(title, citeUrl, siteName);
				results.add(result);
				

			} catch (IndexOutOfBoundsException e) {
//				e.printStackTrace();
			}
		}

		return results;
	}

	public static String removeQueryParams(String url) {
		int queryStartIndex = url.indexOf('?');
		if (queryStartIndex != -1) {
			url = url.substring(0,queryStartIndex);
		}

		return url;
	}
	
	public static String getRedirectedUrl(String originalUrl) throws IOException {
        URL url = new URL(originalUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);  // 禁止自動重定向
        connection.setRequestMethod("HEAD");
        connection.connect();
        
        String location = connection.getHeaderField("Location");
        if (location != null) {
            return location;  // 返回重定向的 URL
        }
        return originalUrl;  // 如果沒有重定向則返回原 URL
    }
	
	public static void main(String[] args) {
		try {
			GoogleQuery googleQuery = new GoogleQuery("二戰");
			List<SearchResult> results = googleQuery.query();
			for (SearchResult result : results) {
				System.out.println(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
