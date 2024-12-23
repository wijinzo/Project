package org.finalproject.book.Keyword;

import java.util.ArrayList;

public class Keyword {
	private String name;
	private int count;
	private float weight;
	private static final ArrayList<Keyword> bookList = new ArrayList<>();
	
	public Keyword(String name,float weight){
		this.name = name;
		this.weight = weight;
	}
	
	//@Override
	//public String toString(){
	//	return "["+name+","+count+","+weight+"]";
	//}
	
	public Keyword() {
		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
    	return name;
    }
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public float getWeight(){
    	return weight;
    }
	
	static {
		bookList.add(new Keyword("書籍", 10));
		bookList.add(new Keyword("出版社", 10));
		bookList.add(new Keyword("書評", 6));
		bookList.add(new Keyword("內容簡介", 7));
		bookList.add(new Keyword("作者", 7));
		bookList.add(new Keyword("書本主題", 7));
		bookList.add(new Keyword("書本類型", 8));
		bookList.add(new Keyword("書店", 7));
		bookList.add(new Keyword("選書", 6));
		bookList.add(new Keyword("贈書", 4));
		bookList.add(new Keyword("小說", 5));
		bookList.add(new Keyword("書摘", 7));
		bookList.add(new Keyword("電子書", 5));
		bookList.add(new Keyword("文學", 5));
		bookList.add(new Keyword("節錄", 5));
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
    public int getCount() {
       	return count;
    }
  
    public void addCount() {
    	count++;
    }
    
    public ArrayList<Keyword> getBookList(){
    	return bookList;
    }
}