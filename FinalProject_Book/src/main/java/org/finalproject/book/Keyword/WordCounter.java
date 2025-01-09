package org.finalproject.book.Keyword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WordCounter {
	private String urlStr;
	private String content;
	private ArrayList<Keyword> keywords = new ArrayList<Keyword>();

	public WordCounter(String urlStr,String content) {
		this.urlStr = urlStr;
		this.content = content;
	}

	// 計算單個關鍵字出現次數
    public static int countKeywordOccurrences(String content, String keyword) {
        int count = 0;
        int index = 0;
        content = content.toLowerCase(); // 忽略大小寫
        keyword = keyword.toLowerCase(); // 忽略大小寫
        while ((index = content.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    // 計算所有關鍵字的出現次數
    public static void calculateKeywordCounts(String content, ArrayList<Keyword> keywords) {
        for (Keyword keyword : keywords) {
            int count = countKeywordOccurrences(content, keyword.getWord());
            keyword.setCount(count);
        }
    }

}