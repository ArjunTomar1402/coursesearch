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

    /**
     * Main search endpoint with fuzzy search support
     * Now supports typos like "sciens" matching "science"
     */
    @GetMapping
    public List<CourseDocument> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge
    ) {
        return courseSearchService.search(keyword, minAge, maxAge);
    }

    /**
     * Native Elasticsearch fuzzy search endpoint
     */
    @GetMapping("/native")
    public List<CourseDocument> searchNative(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge
    ) {
        return courseSearchService.searchNative(keyword, minAge, maxAge);
    }

    /**
     * Debug endpoint - returns all courses in the index
     */
    @GetMapping("/debug/all")
    public List<CourseDocument> getAllCourses() {
        return courseSearchService.getAllCourses();
    }

    /**
     * Debug endpoint - exact search without fuzzy matching
     */
    @GetMapping("/debug/exact")
    public List<CourseDocument> searchExact(@RequestParam String keyword) {
        return courseSearchService.searchExact(keyword);
    }

    /**
     * Autocomplete endpoint - returns course titles that start with prefix
     * Example: /api/search/autocomplete?prefix=sci
     */
    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam String prefix) {
        return courseSearchService.autocomplete(prefix);
    }

    /**
     * Fuzzy autocomplete endpoint - handles typos in the prefix
     * Example: /api/search/fuzzy-autocomplete?prefix=sciens
     */
    @GetMapping("/fuzzy-autocomplete")
    public List<String> fuzzyAutocomplete(@RequestParam String prefix) {
        return courseSearchService.fuzzyAutocomplete(prefix);
    }
}