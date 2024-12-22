package org.finalproject.book.Keyword;

import java.util.ArrayList;
import java.util.List;

public class Keyword {
    private int weight;
    private int count;
	public String word;

    public Keyword(String word, int weight) {
        this.word = word;
        this.weight = weight;
        this.count = 0;
    }

    public int incrementCount() {
        return count++;
    }   
    
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;   
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }



     public static ArrayList<Keyword> getDefaultKeywords() {
        ArrayList<Keyword> keywords = new ArrayList<>();
        keywords.add(new Keyword("書籍", 10));
        keywords.add(new Keyword("出版社", 10));
        keywords.add(new Keyword("書本類型", 8));
        keywords.add(new Keyword("作者", 8));
        keywords.add(new Keyword("內容簡介", 7));
        keywords.add(new Keyword("書本主題", 7));
        keywords.add(new Keyword("書店", 7));
        keywords.add(new Keyword("書摘", 7));
        keywords.add(new Keyword("書評", 6)); 
        keywords.add(new Keyword("選書", 6));
        keywords.add(new Keyword("小說", 5));
        keywords.add(new Keyword("電子書", 5));
        keywords.add(new Keyword("文學", 5));
        keywords.add(new Keyword("節錄", 5));
        keywords.add(new Keyword("贈書", 4));
        return keywords;
    }
}