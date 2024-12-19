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

	public WordCounter(String urlStr) {
		this.urlStr = urlStr;
	}

	private String fetchContent() throws IOException {
		URL url = new URL(this.urlStr);
		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String retVal = "";

		String line = null;

		while ((line = br.readLine()) != null) {
			retVal = retVal + line + "\n";
		}

		return retVal;
	}

	public void countKeyword(Keyword keyword) throws IOException {
		if (content == null) {
			content = fetchContent();
		}

		// To do a case-insensitive search, we turn the whole content and keyword into
		// upper-case:
		content = content.toUpperCase();
		String searchKeyword = keyword.name.toUpperCase();

		int fromIdx = 0;
		int found = -1;

		while ((found = content.indexOf(searchKeyword, fromIdx)) != -1) {
			keyword.addCount();
			fromIdx = found + searchKeyword.length();
		}
	    System.out.println("Keyword: " + keyword.name + " found " + keyword.count + " times");


	}

}