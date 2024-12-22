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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WebPageProcessor {

    // 抓取網頁內容
    public String fetchContentFromUrl(String url) throws IOException {
        StringBuilder retVal = new StringBuilder();

        URL u = new URL(url);
        URLConnection conn = u.openConnection();
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
    public ArrayList<WebPage> fetchLinksWithTitleContainingKeyword(String url, String searchKeyword, int[] totalLinksChecked) throws IOException {
        ArrayList<WebPage> links = new ArrayList<>();
        String content = fetchContentFromUrl(url);
        Document doc = Jsoup.parse(content);
        
        Elements linkElements = doc.select("a[href^='http']");
        
        for (Element link : linkElements) {
            totalLinksChecked[0]++; // 計算總共檢查的連結數量
            String linkHref = link.attr("abs:href");
            String linkTitle = link.attr("title");
            String linkText = link.text();
            

            // 過濾掉無效的 JavaScript 連結和空連結
            if (!linkHref.startsWith("javascript:") && !linkHref.isEmpty()) {
                // 檢查連結 title 或文本是否包含搜尋關鍵字
                if ((linkTitle != null && linkTitle.contains(searchKeyword)) || linkText.contains(searchKeyword)) {
                    links.add(new WebPage(linkHref, linkTitle));
                }
            }
        }


        return links;
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
                int[] totalLinksChecked = {0}; // 用於計算總共檢查的連結數量
                ArrayList<WebPage> linksWithTitleContainingKeyword = processor.fetchLinksWithTitleContainingKeyword(url, searchKeyword, totalLinksChecked);               
                System.out.println("總共檢查的連結數量: " + totalLinksChecked[0]);
                System.out.println("包含搜索詞子連結:");
                if (linksWithTitleContainingKeyword.isEmpty()) {
                    System.out.println("無有效連結");
                } else {
                    for (WebPage link : linksWithTitleContainingKeyword) {
                        System.out.println(link.getUrl());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}