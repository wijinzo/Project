package org.finalproject.book.service;

import org.finalproject.book.web.WebTree;

public class SearchResult {
	private String title;
	private String url;
	private String siteName;
    private String description;
	private WebTree tree = null;

	public SearchResult(String title, String url, String description) {
		this.title = title;
		this.url = url;
        this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTree(WebTree tree) {
		this.tree = tree;
	}

	public String getUrl() {
		return url;
	}
	
	public WebTree getTree() {
		return tree;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	@Override
	public String toString() {
		return "SearchResult{" + "title='" + title + '\'' + ", url='" + url + '\'' + ", siteName='" + siteName + '\'' + ", description='" + description + '\''
				+ '}';
	}
}