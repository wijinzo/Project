package org.finalproject.book.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
			String mainKeyword = " 書";
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
		URLConnection conn = u.openConnection();
		// set HTTP header
		conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");
		InputStream in = conn.getInputStream();

		InputStreamReader inReader = new InputStreamReader(in, "utf-8");
		BufferedReader bufReader = new BufferedReader(inReader);
		String line = null;

		while ((line = bufReader.readLine()) != null) {
			retVal.append(line);
		}
		return retVal.toString();
	}

	public ArrayList<SearchResult> query() throws IOException {
        if (content == null) {
            content = fetchContent();
        }

        ArrayList<SearchResult> results = new ArrayList<>();

		// using Jsoup analyze html string
		Document doc = Jsoup.parse(content);
		// select particular element(tag) which you want
		Elements lis = doc.select("div");
		lis = lis.select(".kCrYT");

		for (Element li : lis) {
			try {
				String citeUrl = li.select("a").get(0).attr("href").replace("/url?q=", "");
				String title = li.select("a").get(0).select(".vvjwJb").text();
				String siteName = li.select("cite").text();


				if (title.equals("")) {
					continue;
				}
				
				citeUrl = URLDecoder.decode(citeUrl, "UTF-8");// 原本的url不能訪問，要先解碼
				citeUrl = removeQueryParams(citeUrl);
				
				SearchResult result = new SearchResult(title, citeUrl);
				results.add(result);
				

			} catch (IndexOutOfBoundsException e) {
//				e.printStackTrace();
			}
		}

		return results;
	}

	public static String removeQueryParams(String url) {
		int queryStartIndex = url.indexOf('&');
		if (queryStartIndex == -1) {
			queryStartIndex = url.indexOf('?');
		}

		return queryStartIndex != -1 ? url.substring(0, queryStartIndex) : url;
	}
	
	public static void main(String[] args) {
		try {
			GoogleQuery googleQuery = new GoogleQuery("二戰");
			ArrayList<SearchResult> results = googleQuery.query();
			for (SearchResult result : results) {
				System.out.println(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}