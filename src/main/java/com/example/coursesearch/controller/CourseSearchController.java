package com.example.coursesearch.controller;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.search.CourseSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    public CourseSearchController(CourseSearchService courseSearchService) {
        this.courseSearchService = courseSearchService;
    }

    @GetMapping
    public List<CourseDocument> search(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double price
    ) {
        return courseSearchService.search(keyword, minAge, maxAge, type, category, price);
    }
}
