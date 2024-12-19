package org.finalproject.book.controller;

import java.io.IOException;
import java.util.HashMap;

import org.finalproject.book.service.GoogleQuery;
import org.finalproject.book.service.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BookSearchController {

    @GetMapping("/")
    public String index() {
        return "index";  // 返回 index.html
    }

    @PostMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {       
        try {
            GoogleQuery googleQueryService = new GoogleQuery(keyword);
            List<SearchResult> searchResults = googleQueryService.query();
            System.out.println("搜尋結果: " + searchResults);  // 打印結果到控制台
            model.addAttribute("results", searchResults);  // 將結果添加到模型中
            System.out.println("Result Titles: " + searchResults.stream().map(SearchResult::getTitle).collect(Collectors.toList()));

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "查詢失敗，請稍後再試。");
        }
        return "result";  // 返回結果頁面
    }

}