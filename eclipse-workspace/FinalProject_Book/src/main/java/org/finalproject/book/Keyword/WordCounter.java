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

//	private String fetchContent() throws IOException {
//		URL url = new URL(this.urlStr);
//		URLConnection conn = url.openConnection();
//		InputStream in = conn.getInputStream();
//		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//		String retVal = "";
//
//		String line = null;
//
//		while ((line = br.readLine()) != null) {
//			retVal = retVal + line + "\n";
//		}
//
//		return retVal;
//	}

//	public void countKeyword(Keyword keyword) throws IOException {
////		if (content == null) {
////			content = fetchContent();
////		}
//
//		// To do a case-insensitive search, we turn the whole content and keyword into
//		// upper-case:
//		content = content.toUpperCase();
//		String searchKeyword = keyword.getWord().toUpperCase();
//
//		int fromIdx = 0;
//		int found = -1;
//
//		while ((found = content.indexOf(searchKeyword, fromIdx)) != -1) {
//			//keyword.addCount();
//			fromIdx = found + searchKeyword.length();
//		}
//	    System.out.println("Keyword: " + keyword.getWord() + " found " + keyword.getCount() + " times");
//
//
//	}

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