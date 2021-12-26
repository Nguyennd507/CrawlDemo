package com.example.mockproject.elastic.repository;

import com.example.mockproject.elastic.model.RealEstateModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IRealEstateESRepository extends ElasticsearchRepository<RealEstateModel, Long> {
}
