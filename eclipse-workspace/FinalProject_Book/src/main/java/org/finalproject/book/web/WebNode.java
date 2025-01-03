package org.finalproject.book.web;
import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.util.ArrayList;
import org.finalproject.book.Keyword.Keyword;

public class WebNode
{
	public WebNode parent;
	public ArrayList<WebNode> children;
	public WebPage webPage;
	public double nodeScore;// This node's score += all its children's nodeScore

	public WebNode(WebPage webPage)
	{
		this.webPage = webPage;
		this.children = new ArrayList<WebNode>();
	}

	public void setNodeScore(ArrayList<Keyword> keywords) throws IOException{
		
		webPage.setScore(keywords);
		nodeScore += webPage.getScore() ;
		for(WebNode child:children) {
			child.webPage.setScore(keywords);
			nodeScore += child.webPage.getScore();
		}
		
		
	}

	public void addChild(WebNode child){
		// add the WebNode to its children list
		this.children.add(child);
		child.parent = this;
	}

	public boolean isTheLastChild(){
		if (this.parent == null)
			return true;
		ArrayList<WebNode> siblings = this.parent.children;

		return this.equals(siblings.get(siblings.size() - 1));
	}

	public int getDepth(){
		int retVal = 1;
		WebNode currNode = this;
		while (currNode.parent != null)
		{
			retVal++;
			currNode = currNode.parent;
		}
		return retVal;
	}
}
