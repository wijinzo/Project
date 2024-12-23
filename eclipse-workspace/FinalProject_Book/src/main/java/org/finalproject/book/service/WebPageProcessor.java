package org.finalproject.book.service;

import org.finalproject.book.web.WebNode;
import org.finalproject.book.web.WebPage;
import org.finalproject.book.web.WebTree;
import org.jsoup.Jsoup;
import org.finalproject.book.Keyword.Keyword;
import org.finalproject.book.Keyword.WordCounter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WebPageProcessor {

	public WebPageProcessor() {

	}

	// 抓取網頁內容
	public String fetchContentFromUrl(String url) throws IOException {
		StringBuilder retVal = new StringBuilder();

		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();

		// 設置 User-agent，防止被認定為爬蟲
		conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");

		// 獲取 HTTP 狀態碼
		int statusCode = conn.getResponseCode();

		// 如果狀態碼是 403，則代表禁止訪問，打印並跳過
		if (statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
			System.out.println("無法處理連結: " + url + " (403 Forbidden)，將跳過此 URL");
			return ""; // 返回空字符串，跳過該 URL
		}

		try (InputStream in = conn.getInputStream();
				InputStreamReader inReader = new InputStreamReader(in, "utf-8");
				BufferedReader bufReader = new BufferedReader(inReader)) {

			String line;
			while ((line = bufReader.readLine()) != null) {
				retVal.append(line); // 拼接每一行的內容
			}

		} catch (IOException e) {
			System.out.println("無法處理連結: " + url + " 錯誤: " + e.getMessage());
			return ""; // 返回空字符串，跳過異常 URL
		}

		return retVal.toString(); // 返回內容
	}

	// 計算總分
	public int calculateTotalScore(ArrayList<Keyword> keywords) {
		int totalScore = 0;
		for (Keyword keyword : keywords) {
			totalScore += keyword.getCount() * keyword.getWeight();
		}
		return totalScore;
	}

	// 提取純文本內容
	public String extractText(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		return doc.text();
	}

	public ArrayList<WebPage> fetchLinksWithTitleContainingKeyword(String url, String searchKeyword,
			int[] totalLinksChecked) throws IOException {
		ArrayList<WebPage> links = new ArrayList<>();
		String content = fetchContentFromUrl(url); // 抓取主頁內容
		Document doc = Jsoup.parse(content);

		Elements linkElements = doc.select("a[href^='http']"); // 篩選出有效的 HTTP 或 HTTPS 連結

		for (Element link : linkElements) {
			totalLinksChecked[0]++; // 累加檢查的連結數量
			String linkHref = link.attr("abs:href"); // 取得連結的完整 URL
			String linkTitle = link.attr("title"); // 取得連結的 title
			String linkText = link.text(); // 取得連結的文字內容

			try {
				// 過濾掉 JavaScript 和空連結
				if (!linkHref.startsWith("javascript:") && !linkHref.isEmpty()) {
					// 檢查連結的 title 或 text 是否包含關鍵字
					if ((linkTitle != null && linkTitle.contains(searchKeyword)) || linkText.contains(searchKeyword)) {
						String subContent = fetchContentFromUrl(linkHref); // 抓取子頁內容
						links.add(new WebPage(linkHref, subContent)); // 創建 WebPage 並添加到結果列表
					}
				}
			} catch (IOException e) {
				System.out.println("無法處理連結: " + linkHref);
				continue; // 跳過異常連結並處理剩餘部分
			}
		}

		return links; // 返回符合條件的子連結列表
	}

	public void calculateTreeScores(WebTree webTree, ArrayList<Keyword> keywords) throws IOException {
		// 設置分數，從樹的底部向上遞歸計算
		webTree.setPostOrderScore(keywords);
	}

	public void getWebSummary(String searchKeyword, ArrayList<SearchResult> results) {
		try {
			for (SearchResult result : results) {
				// Retrieving the URL from the SearchResult
				String url = result.getUrl();
				String content = fetchContentFromUrl(url);

				if (content == null || content.isEmpty()) {
					System.out.println("Failed to fetch content or content is empty from: " + url);
					continue; // Skip if no content is retrieved
				}

				String textContent = extractText(content);
				WebPage rootPage = new WebPage(result.getUrl(), textContent);
//				WebNode root = new WebNode(rootPage);
				WebTree tree = new WebTree(rootPage);
				// Extracting text content

				// Defining default keywords
				ArrayList<Keyword> keywords = Keyword.getDefaultKeywords();

				// Counting the occurrences of the keywords
//				WordCounter.calculateKeywordCounts(textContent, keywords);

				// Calculating total score based on the keywords found
//				int totalScore = calculateTotalScore(keywords);
				tree.root.setNodeScore(keywords);
//				 Printing out the results
				System.out.println("URL: " + rootPage.getUrl());
				System.out.println("Title: " + result.getTitle());
				System.out.println("Site Name: " + result.getSiteName());
				System.out.println(
						"First 500 characters: " + textContent.substring(0, Math.min(500, textContent.length()))); // First
																													// 500
																													// characters
				for (Keyword keyword : keywords) {
					System.out.println("Keyword: " + keyword.getWord() + ", Count: " + keyword.getCount());
				}

//	            System.out.println("Total Score: " + totalScore);

				// Fetch links that contain the keyword in the title
				int[] totalLinksChecked = { 0 }; // Tracks the number of links checked
				ArrayList<WebPage> subWebPages = fetchLinksWithTitleContainingKeyword(url, searchKeyword,
						totalLinksChecked);
				System.out.println("Total links checked: " + totalLinksChecked[0]);
				System.out.println("Links containing the search keyword:");
				if (subWebPages.isEmpty()) {
					System.out.println(url + " no valid links found.");
				} else {
					for (WebPage childPage : subWebPages) {
						WebNode childNode = new WebNode(childPage);
						String link = childPage.getUrl();
						System.out.println(link);
						tree.addChild(childNode);
						// For each link, fetch its content and perform keyword extraction
						String childContent = childPage.getContent();
						if (childContent == null || childContent.isEmpty()) {
							System.out.println("No content found for the link: " + link);
							continue;
						}

						String linkTextContent = extractText(childContent);
						tree.setPostOrderScore(keywords);
//						WordCounter.calculateKeywordCounts(linkTextContent, linkKeywords);

						// Calculate total score for the link content
//						int linkTotalScore = calculateTotalScore(linkKeywords);
						
						// Printing the results for the link
						for (Keyword keyword : keywords) {
							System.out.println("Link Keyword: " + keyword.getWord() + ", Count: " + keyword.getCount());
						}
					}
				}
				System.out.println("Total Score: " + tree.root.nodeScore);
			}
		} catch (Exception e) {
			System.out.println("Error processing the search results: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			String searchKeyword = "二戰";
			GoogleQuery googleQuery = new GoogleQuery(searchKeyword);
			ArrayList<SearchResult> results = googleQuery.query();
			WebPageProcessor processor = new WebPageProcessor();
			WebTree webTree = new WebTree(null); // 初始化空的 WebTree
			for (SearchResult result : results) {
				String url = result.getUrl();
				String content = processor.fetchContentFromUrl(url);
				if (content == null || content.isEmpty()) {
					System.out.println("Failed to fetch content or content is empty from: " + url);
					continue;
				}
				WebPage webPage = new WebPage(url, content);
				WebNode node = new WebNode(webPage);
				// 提取純文本內容
				String textContent = processor.extractText(content);
				// 設置關鍵字
				ArrayList<Keyword> keywords = Keyword.getDefaultKeywords();
				// 計算關鍵字出現次數
				WordCounter.calculateKeywordCounts(textContent, keywords);

				if (webTree.getRoot() == null) {
					webTree = new WebTree(webPage);
				} else {
					webTree.root.addChild(node); // 作為根的子節點
				}
				// 抓取子連結
				int[] totalLinksChecked = { 0 };
				ArrayList<WebPage> linksWithTitleContainingKeyword = processor.fetchLinksWithTitleContainingKeyword(url,
						searchKeyword, totalLinksChecked);
				for (WebPage link : linksWithTitleContainingKeyword) {
					WebNode childNode = new WebNode(link);
					node.addChild(childNode);
				}
			}
			// 計算整棵樹的分數
			ArrayList<Keyword> keywords = Keyword.getDefaultKeywords();
			processor.calculateTreeScores(webTree, keywords);
			// 按分數排序並打印樹的結構
//			webTree.sortNodesByScore(webTree.getRoot());
//			webTree.printTree(webTree.getRoot(), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}