package org.finalproject.book.web;

import java.io.IOException;
import java.util.ArrayList;

import org.finalproject.book.Keyword.Keyword;
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
//	public WebPage(String url, String name){
//		this.url = url;
//		this.name = name;
//		this.counter = new WordCounter(url);
//	}

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
		// YOUR TURN
		// 1. calculate the score of this webPage
		for (Keyword keyword : keywords) {
			counter.countKeyword(keyword);
			score += keyword.getCount() * keyword.getWeight();
		}

	}

}