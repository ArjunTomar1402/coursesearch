package com.example.coursesearch.search;

import com.example.coursesearch.document.CourseDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public CourseSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<CourseDocument> search(String keyword, Integer minAge, Integer maxAge, String type, String category, Double price) {
        Criteria criteria = new Criteria("title").matches(keyword);

        if (minAge != null) {
            criteria = criteria.and(new Criteria("minAge").greaterThanEqual(minAge));
        }
        if (maxAge != null) {
            criteria = criteria.and(new Criteria("maxAge").lessThanEqual(maxAge));
        }
        if (type != null && !type.isBlank()) {
            criteria = criteria.and(new Criteria("type").is(type));
        }
        if (category != null && !category.isBlank()) {
            criteria = criteria.and(new Criteria("category").is(category));
        }
        if (price != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(price));
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(PageRequest.of(0, 20));

        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);
        return hits.stream().map(SearchHit::getContent).toList();
    }
}
