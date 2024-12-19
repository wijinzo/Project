package org.finalproject.book.Keyword;

import java.util.HashMap;
import java.util.Map;

public class WordCounter {
    public static Map<String, Integer> countWords(String text, String[] keywords) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String keyword : keywords) {
            int count = 0;
            int index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) {
                count++;
                index += keyword.length();
            }
            wordCounts.put(keyword, count);
        }
        return wordCounts;
    }

    public static void countKeyword(Keyword keyword, String content, boolean caseSensitive) {
        int count = 0;
        String[] words = content.split("\\W+");

        for (String word : words) {
            if (caseSensitive) {
                if (word.equals(keyword.getWord())) {
                    count++;
                }
            } else {
                if (word.equalsIgnoreCase(keyword.getWord())) {
                    count++;
                }
            }
        }
        keyword.setCount(count);
    }
}