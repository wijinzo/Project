package org.finalproject.book.Keyword;

public class Keyword {
	public String name;
    public int count;
    public float weight;
    
    public Keyword(String name, float weight){
      this.name = name;
      this.count = 0;
      this.weight = weight;
    }
    
//    @Override
//    public String toString(){
//    	return "["+name+","+count+","+weight+"]";
//    }
//    

    public int getCount(){
    	return count;
    }
    public String getName(){
    	return name;
    }
    public float getWeight(){
    	return weight;
    }
    
    public void addCount() {
    	this.count = count++;
    }
   
}