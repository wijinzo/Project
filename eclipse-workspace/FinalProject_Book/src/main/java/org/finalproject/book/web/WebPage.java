package org.finalproject.book.web;

import java.io.IOException;
import java.util.ArrayList;

import org.finalproject.book.Keyword.Keyword;
import org.finalproject.book.Keyword.WordCounter;
import org.finalproject.book.Keyword.WordCounter;

public class WebPage {
	private String url;
	private WordCounter counter;
	private double score;
	private String content;

	public WebPage(String url, String content) {
		this.url = url;
		this.content = content;
		this.counter = new WordCounter(url, content);

	}

	public String getUrl() {
		return url;
	}

	public String getContent() {
		return content;
	}
	
	public double getScore() {
		return score;
	}

	public void setScore(ArrayList<Keyword> keywords) throws IOException {
		score = 0;
		for (Keyword keyword : keywords) {
			counter.calculateKeywordCounts(content,keywords);
			score += keyword.getCount() * keyword.getWeight();
		}

	}
	

}