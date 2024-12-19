package org.finalproject.book.service;

import org.finalproject.book.web.WebPage;
import org.finalproject.book.Keyword.Keyword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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

    // 計算關鍵字出現次數
    public void calculateKeywordCounts(WebPage webPage, List<Keyword> keywords) {
        String content = webPage.getContent();
        for (Keyword keyword : keywords) {
            int count = countKeywordOccurrences(content, keyword.getWord());
            keyword.setCount(count);
        }
    }

    // 計算單個關鍵字出現次數
    private int countKeywordOccurrences(String content, String keyword) {
        int count = 0;
        String[] words = content.split("\\W+");
        for (String word : words) {
            if (word.equalsIgnoreCase(keyword)) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        try {
            GoogleQuery googleQuery = new GoogleQuery("二戰");
            List<SearchResult> results = googleQuery.query();

            WebPageProcessor processor = new WebPageProcessor();
            for (SearchResult result : results) {
                // 調用 SearchResult 中儲存的網址
                String content = processor.fetchContentFromUrl(result.getUrl());
                WebPage webPage = new WebPage(result.getUrl(), content);

                // 設置關鍵字
                List<Keyword> keywords = Keyword.getDefaultKeywords();
                processor.calculateKeywordCounts(webPage, keywords);

                // 打印結果
                System.out.println("網址: " + webPage.getUrl());
                System.out.println("網站: " + result.getTitle());
                for (Keyword keyword : keywords) {
                    System.out.println("Keyword: " + keyword.getWord() + ", Count: " + keyword.getCount());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
