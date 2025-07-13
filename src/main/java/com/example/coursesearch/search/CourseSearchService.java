package com.example.coursesearch.search;

import com.example.coursesearch.document.CourseDocument;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public CourseSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<CourseDocument> search(String keyword) {
        Criteria criteria = new Criteria()
                .or("title").matches(keyword)
                .or("description").matches(keyword)
                .or("category").matches(keyword);

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        return hits.get().map(hit -> hit.getContent()).collect(Collectors.toList());
    }
}
