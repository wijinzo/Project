package org.finalproject.book.web;
import java.io.IOException;
import java.util.ArrayList;

import org.finalproject.book.Keyword.Keyword;

public class WebTree {
	public WebNode root;

	public WebTree(WebPage rootPage) {
		this.root = new WebNode(rootPage);
	}

	public void setPostOrderScore(ArrayList<Keyword> keywords) throws IOException {
		setPostOrderScore(root, keywords);
	}

	private void setPostOrderScore(WebNode startNode, ArrayList<Keyword> keywords) throws IOException {
		// YOUR TURN
		// 3. compute the score of children nodes via post-order, then setNodeScore for
		// startNode
		int score = 0;
		for (WebNode child : startNode.children) {
			child.setNodeScore(keywords);
		}
		root.setNodeScore(keywords);
		root.nodeScore += score;

	}

	public WebNode getRoot() {
		return root;
	}
	
	public void addChild(WebNode child){
		// add the WebNode to its children list
		root.addChild(child);
	}
	
//	private String repeat(String str, int repeat) {
//		String retVal = "";
//		for (int i = 0; i < repeat; i++) {
//			retVal += str;
//		}
//		return retVal;
//	}
}