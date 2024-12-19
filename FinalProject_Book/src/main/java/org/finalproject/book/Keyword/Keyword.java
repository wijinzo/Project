package org.finalproject.book.Keyword;

import java.util.ArrayList;

public class Keyword {
	public String name;
	public int count;
	public double weight;
	
	public Keyword(String name,float weight){
		this.name = name;
		this.count = 0;
		this.weight = weight;
	}
	public Keyword() {
		// TODO Auto-generated constructor stub
	}
	

	//@Override
	//public String toString(){
	//	return "["+name+","+count+","+weight+"]";
	//}
	
    public int getCount()
    {
    	return count;
    }
    
    public String getName()
    {
    	return name;
    }
    
    public double getWeight()
    {
    	return weight;
    }
    
    public void addCount() {
    	count++;
    }
    
}