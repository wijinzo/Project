package org.finalproject.book.service;

import org.finalproject.book.web.WebNode;
import org.finalproject.book.web.WebPage;
import org.finalproject.book.web.WebTree;
import org.jsoup.Jsoup;
import org.apache.logging.log4j.util.PropertySource.Comparator;
import org.finalproject.book.Keyword.Keyword;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WebPageProcessor {
	private ArrayList<Keyword> keywords = Keyword.getDefaultKeywords();
	HashSet<String> visitedLinks = new HashSet<>();

	public WebPageProcessor() {

	}

	// 抓取網頁內容
	public String fetchContentFromUrl(String url) {
		StringBuilder retVal = new StringBuilder();

		// URL
		try {
			new URL(url); // 驗證 URL 格式
		} catch (MalformedURLException e) {
			System.out.println("無效的 URL: " + url);
			return ""; // 返回空字符串，跳過無效的 URL
		}

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("User-agent", "Chrome/107.0.5304.107");

			int statusCode = conn.getResponseCode();

			if (statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
				System.out.println("無法處理連結: " + url + " (403 Forbidden)，將跳過此 URL");
				return ""; 
			} else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
				System.out.println("無法處理連結: " + url + " (404 Not Found)，將跳過此 URL");
				return ""; 
			} else if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				System.out.println("無法處理連結: " + url + " (500 Internal Server Error)，將跳過此 URL");
				return ""; 
			}

			try (InputStream in = conn.getInputStream();
					InputStreamReader inReader = new InputStreamReader(in, "utf-8");
					BufferedReader bufReader = new BufferedReader(inReader)) {

				String line;
				while ((line = bufReader.readLine()) != null) {
					retVal.append(line); // 拼接每一行的內容
				}

			} catch (Exception e) {
				System.out.println("錯誤處理 URL: " + url + " 錯誤: " + e.getMessage());
				return ""; // 返回空字符串，跳過所有異常的 URL
			}

		} catch (Exception e) {
			System.out.println("無法連接 URL: " + url + " 錯誤: " + e.getMessage());
			return ""; 
		} finally {
			if (conn != null) {
				conn.disconnect(); // 確保關閉連線
			}
		}

		return retVal.toString(); // 返回內容
	}

	// real web content, not html
	public String extractText(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		return doc.text();
	}

	// get child link from title and text contain SearchKeyword
	public ArrayList<WebPage> fetchChildLinks(String url, String searchKeyword, int[] totalLinksChecked)
			throws Exception {
		ArrayList<WebPage> links = new ArrayList<>();
		String content = fetchContentFromUrl(url); // main page
		Document doc = Jsoup.parse(content);

		Elements linkElements = doc.select("a[href^='http']"); // 篩選出有效的 HTTP 或 HTTPS 連結

		for (Element link : linkElements) {
			totalLinksChecked[0]++; // the links number be checked
			String linkHref = link.attr("abs:href"); // get children link
			String linkTitle = link.attr("title"); // the links' title
			String linkText = link.text(); // the links' text (!= content)

			try {
				// exclude JavaScript and empty links
				if (!linkHref.startsWith("javascript:") && !linkHref.isEmpty()
						&& !(linkHref.toLowerCase().endsWith(".jpg") || linkHref.toLowerCase().endsWith(".png"))) {

					// Fetch links that contain the keyword in the title or text
					if ((linkTitle != null && linkTitle.contains(searchKeyword)) || linkText.contains(searchKeyword)) {
						String subContent = fetchContentFromUrl(linkHref); // 抓取子頁內容
						links.add(new WebPage(linkHref, subContent)); // create WebPage and add to links(arrayList)

					}
				}
			} catch (Exception e) {
				continue; // 跳過異常連結並處理剩餘部分
			}
		}

		return links; // 返回符合條件的子連結列表
	}

	public void sortTree(ArrayList<SearchResult> results) {
		results.sort((result1, result2) -> Double.compare(result2.getTree().root.nodeScore,
				result1.getTree().root.nodeScore));
	}

	public void setChildsPage(ArrayList<WebPage> childPages, WebTree tree, String searchKeyword,
			int[] totalLinksChecked, int currentDepth) throws Exception {
		if (childPages.isEmpty() || currentDepth > 2) { 
			System.out.println("no valid links found or maxDepth");
			return;
		}

		int validLinkCount = 0; 

		for (WebPage childPage : childPages) {
			if (validLinkCount >= 3) { 
				break;
			}

			String link = childPage.getUrl();
			String baseLink = link.split("#")[0];
			if (visitedLinks.contains(baseLink)) {
	            System.out.println("Skipping already visited link: " + link);
	            continue; 
	        }

			System.out.println("Processing link (Depth " + currentDepth + "): " + link);

			String childContent;
			try {
				childContent = fetchContentFromUrl(link); 
			} catch (Exception e) {
				System.out.println("無法處理連結: " + link);
				continue; 
			}

			if (childContent == null || childContent.isEmpty()) {
				System.out.println("No content found for the link: " + link);
				continue; 
			}

			WebNode childNode = new WebNode(childPage);
			tree.addChild(childNode);

			tree.setPostOrderScore(keywords);

			for (Keyword keyword : keywords) {
				System.out.println("LinkKeyword: " + keyword.getWord() + ", Count: " + keyword.getCount());
			}

			validLinkCount++;
			visitedLinks.add(baseLink);

			ArrayList<WebPage> grandChildPages = fetchChildLinks(link, searchKeyword, totalLinksChecked);
			setChildsPage(grandChildPages, tree, searchKeyword, totalLinksChecked, currentDepth + 1); // 遞迴下一層
		}
	}

	public ArrayList<SearchResult> setScore(String searchKeyword, ArrayList<SearchResult> results) {
		try {
			for (SearchResult result : results) {
				// Retrieving the URL from the SearchResult
				String url = result.getUrl();
				String content = fetchContentFromUrl(url);

				if (content != null) {

					String textContent = extractText(content);
					// System.out.println("textContent: "+textContent);
					WebPage rootPage = new WebPage(result.getUrl(), textContent);
					WebTree tree = new WebTree(rootPage);


					tree.root.setNodeScore(keywords);
//				    Printing out the results
					System.out.println("URL: " + rootPage.getUrl());
					System.out.println("Title: " + result.getTitle());
					System.out.println("Description: " + result.getDescription());

					for (Keyword keyword : keywords) {
						System.out.println("Keyword: " + keyword.getWord() + ", Count: " + keyword.getCount());
					}

					// Fetch links that contain the keyword in the title
					int[] totalLinksChecked = { 0 }; // Tracks the number of links checked
					ArrayList<WebPage> childPages = fetchChildLinks(url, searchKeyword, totalLinksChecked);
					System.out.println("Total links checked: " + totalLinksChecked[0]);
					System.out.println("Links containing the search keyword:");
					/*
					 * if (childPages.isEmpty()) { System.out.println(url +
					 * " no valid links found."); } else { int validLinkCount = 0; // 有效連結計數器 for
					 * (WebPage childPage : childPages) {
					 * 
					 * WebNode childNode = new WebNode(childPage); String link = childPage.getUrl();
					 * System.out.println(link); tree.addChild(childNode); // For each link, fetch
					 * its content and perform keyword extraction String childContent =
					 * childPage.getContent();
					 * 
					 * try { childContent = fetchContentFromUrl(link); } catch (Exception e) {
					 * System.out.println("無法處理連結: " + link); continue; } if (childContent == null
					 * || childContent.isEmpty()) {
					 * System.out.println("No content found for the link: " + link); continue; }
					 * 
					 * String linkTextContent = extractText(childContent);
					 * tree.setPostOrderScore(keywords);
					 * 
					 * // Printing the results for the link
					 * 
					 * for (Keyword keyword : keywords) { System.out.println( "Link Keyword: " +
					 * keyword.getWord() + ", Count: " + keyword.getCount()); } validLinkCount++; //
					 * 增加有效連結計數器 if (validLinkCount >= 3) { break; // 當抓取到三個有效子連結後停止 }
					 * 
					 * } }
					 */
//					setChildsPage(childPages, tree, url, searchKeyword, totalLinksChecked);
					setChildsPage(childPages, tree, searchKeyword, totalLinksChecked, 1);

					result.setTree(tree);
					System.out.println("Total Score: " + tree.root.nodeScore);
					System.out.println("-------------------------------------------------");
					visitedLinks.clear();
				}
			}

		} catch (Exception e) {
			System.out.println("Error processing the search results: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		sortTree(results);
		return results;
	}

}