package org.finalproject.book.service;

import org.finalproject.book.web.WebPage;
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
	        return "";  // 返回空字符串，跳過該 URL
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
	        return "";  // 返回空字符串，跳過異常 URL
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

	// 抓取主頁中的前三個子連結
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

	public void getWeb(String searchKeyword, ArrayList<SearchResult> results) {
		try {
			
			for (SearchResult result : results) {
				// 調用 SearchResult 中儲存的網址
				String url = result.getUrl();
				String content = fetchContentFromUrl(url);
				WebPage webPage = new WebPage(result.getUrl(), content);

				// 提取純文本內容
				String textContent = extractText(content);

				// 設置關鍵字
				ArrayList<Keyword> keywords = Keyword.getDefaultKeywords();

				// 計算關鍵字出現次數
				WordCounter.calculateKeywordCounts(textContent, keywords);

				// 計算總分
				int totalScore = calculateTotalScore(keywords);

				// 打印結果
				System.out.println("網址: " + webPage.getUrl());
				System.out.println("標題: " + result.getTitle());
//				System.out.println("網站名稱: " + result.getSiteName());
//				System.out.println("前500字: " + textContent.substring(0, Math.min(500, textContent.length()))); // 提取前500字
				for (Keyword keyword : keywords) {
					System.out.println("Keyword: " + keyword.getWord() + ", Count: " + keyword.getCount());
				}

				System.out.println("總分: " + totalScore);

				// 抓取主頁中所有 title 或文本包含搜索關鍵字的子連結
				int[] totalLinksChecked = { 0 }; // 用於計算總共檢查的連結數量
				ArrayList<WebPage> linksWithTitleContainingKeyword = fetchLinksWithTitleContainingKeyword(url,
						searchKeyword, totalLinksChecked);
				System.out.println("總共檢查的連結數量: " + totalLinksChecked[0]);
				System.out.println("包含搜索詞子連結:");
				if (linksWithTitleContainingKeyword.isEmpty()) {
					System.out.println("無有效連結");
				} else {
					for (WebPage link : linksWithTitleContainingKeyword) {
						System.out.println(link.getUrl());
//						System.out.println(link.getContent().substring(0, Math.min(500, link.getContent().length())));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		try {
			String searchKeyword = "二戰";
			GoogleQuery googleQuery = new GoogleQuery(searchKeyword);
			ArrayList<SearchResult> results = googleQuery.query();
			
			WebPageProcessor processor = new WebPageProcessor();
			for (SearchResult result : results) {
				// 調用 SearchResult 中儲存的網址
				String url = result.getUrl();
				String content = processor.fetchContentFromUrl(url);
				WebPage webPage = new WebPage(result.getUrl(), content);
				
				// 提取純文本內容
				String textContent = processor.extractText(content);
				
				// 設置關鍵字
				ArrayList<Keyword> keywords = Keyword.getDefaultKeywords();
				
				// 計算關鍵字出現次數
				WordCounter.calculateKeywordCounts(textContent, keywords);
				
				// 計算總分
				int totalScore = processor.calculateTotalScore(keywords);
				
				// 打印結果
				System.out.println("網址: " + webPage.getUrl());
				System.out.println("標題: " + result.getTitle());
				System.out.println("網站名稱: " + result.getSiteName());
				System.out.println("前500字: " + textContent.substring(0, Math.min(500, textContent.length()))); // 提取前500字
				for (Keyword keyword : keywords) {
					System.out.println("Keyword: " + keyword.getWord() + ", Count: " + keyword.getCount());
				}
				
				System.out.println("總分: " + totalScore);
				
				// 抓取主頁中所有 title 或文本包含搜索關鍵字的子連結
				int[] totalLinksChecked = { 0 }; // 用於計算總共檢查的連結數量
				ArrayList<WebPage> linksWithTitleContainingKeyword = processor.fetchLinksWithTitleContainingKeyword(url,
						searchKeyword, totalLinksChecked);
				System.out.println("總共檢查的連結數量: " + totalLinksChecked[0]);
				System.out.println("包含搜索詞子連結:");
				if (linksWithTitleContainingKeyword.isEmpty()) {
					System.out.println("無有效連結");
				} else {
					for (WebPage link : linksWithTitleContainingKeyword) {
						System.out.println(link.getUrl());
						System.out.println(link.getContent().substring(0, Math.min(500, link.getContent().length())));
						
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}