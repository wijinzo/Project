package org.finalproject.book.web;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.finalproject.book.Keyword.Keyword;
import org.finalproject.book.Keyword.WordCounter;

public class WebPage{
	private String url;
    private String content;

    public WebPage(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
	


