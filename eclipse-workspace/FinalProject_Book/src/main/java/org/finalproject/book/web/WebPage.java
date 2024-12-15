package org.finalproject.book.web;
import java.io.IOException;
import java.util.ArrayList;

import org.finalproject.book.Keyword.Keyword;
import org.finalproject.book.Keyword.WordCounter;

public class WebPage{
	public String url;
	public String name;
	public WordCounter counter;
	public double score;

	public WebPage(String url){
		this.url = url;
		this.counter = new WordCounter(this.url);
		
	}
//	public WebPage(String url, String name){
//		this.url = url;
//		this.name = name;
//		this.counter = new WordCounter(url);
//	}

	public void setScore(ArrayList<Keyword> keywords) throws IOException{
		score = 0;
		// YOUR TURN
		// 1. calculate the score of this webPage
		for(Keyword keyword:keywords ) {
			counter.countKeyword(keyword);
			score += keyword.count*keyword.weight;
		}

	}
	
}