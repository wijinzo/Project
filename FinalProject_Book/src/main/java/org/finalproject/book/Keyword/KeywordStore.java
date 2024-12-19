package org.finalproject.book.Keyword;
import java.util.ArrayList;
public class KeywordStore {
	private Keyword keyword;
	private ArrayList<Keyword> bookList;
	
	public KeywordStore(Keyword keyword) {
		this.keyword =keyword;
		this.bookList = new ArrayList<>();
	}
	
	public void setBookList() {
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
}