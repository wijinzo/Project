package org.finalproject.book.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.finalproject.book.service.GoogleQuery;
import org.finalproject.book.service.SearchResult;
import org.finalproject.book.service.WebPageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookSearchController {

    private static final Logger logger = LoggerFactory.getLogger(BookSearchController.class);

    @GetMapping("/")
    public String index() {
        return "index";  // 返回 index.html
    }

    @PostMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        ArrayList<SearchResult> results = new ArrayList<>();  // 初始化以避免空指針錯誤
        try {
            GoogleQuery googleQueryService = new GoogleQuery(keyword);
            WebPageProcessor webPageProcessor = new WebPageProcessor();
            results = googleQueryService.query();
            results = webPageProcessor.fastSetScore(keyword, results);
            
            logger.info("搜尋結果數量: {}", results.size());
            model.addAttribute("results", results);

        } catch (IOException e) {
            logger.error("查詢失敗：", e);
            model.addAttribute("error", "查詢失敗，請稍後再試。");
        }
        return "result";  // 返回結果頁面
    }

}