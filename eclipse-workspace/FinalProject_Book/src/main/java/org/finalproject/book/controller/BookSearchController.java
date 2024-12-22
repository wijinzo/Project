package org.finalproject.book.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.finalproject.book.service.GoogleQuery;
import org.finalproject.book.service.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookSearchController {

    @GetMapping("/")
    public String index() {
        return "index";  // 返回 index.html
    }

    @PostMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        ArrayList<SearchResult> results;//hash map不見了
        try {
            GoogleQuery googleQueryService = new GoogleQuery(keyword);
            results = googleQueryService.query();
            System.out.println("搜尋結果: " + results);  // 打印結果到控制台
            model.addAttribute("results", results);  // 將結果添加到模型中
//            System.out.println("Result Titles: " + results.keySet());

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "查詢失敗，請稍後再試。");
        }
        return "result";  // 返回結果頁面
    }

}