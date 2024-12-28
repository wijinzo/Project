package org.finalproject.book.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	public ArrayList<SearchResult> results;

	public GoogleQuery(String searchKeyword) {
		this.searchKeyword = searchKeyword;
		this.results = new ArrayList<>();
		try {

			String encodeKeyword = java.net.URLEncoder.encode(searchKeyword, "utf-8");
			String mainKeyword = " 網路書店";
			String encodeMainKeyword = java.net.URLEncoder.encode(mainKeyword, "utf-8");

			this.url = "https://www.google.com/search?q=" + encodeKeyword + "+" + encodeMainKeyword + "&oe=utf8&num=20";

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

		List<String> allowedDomains = List.of("bookrep.com.tw", "taaze.tw", "eslite.com", "sanmin.com.tw",
				"store.showwe.tw", "cite.com.tw", "ithome.com.tw", "shop.cwbook.com.tw", "cavesbooks.com.tw",
				"readmoo.com", "books.cw.com.tw", "momoshop.com.tw"); // 允許的域名
		for (Element li : lis) {
			try {
				String citeUrl = li.select("a").get(0).attr("href").replace("/url?q=", "");
				String title = li.select("a").get(0).select(".vvjwJb").text();

				Element parentLis = li.parent().select(".kCrYT").get(1);
				String description = parentLis.select(".BNeawe").text(); // 抓取描述
				String siteName = li.select("cite").text();

				if (title.equals("")) {
					continue;
				}

				citeUrl = URLDecoder.decode(citeUrl, "UTF-8");// 原本的url不能訪問，要先解碼
				citeUrl = removeQueryParams(citeUrl);

				// 過濾不符合條件的網站
				boolean isAllowed = allowedDomains.stream().anyMatch(citeUrl::contains);
				if (!isAllowed) {
					continue;
				}

				SearchResult result = new SearchResult(title, citeUrl, description);
				results.add(result);

			} catch (IndexOutOfBoundsException e) {
				// e.printStackTrace();
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

}