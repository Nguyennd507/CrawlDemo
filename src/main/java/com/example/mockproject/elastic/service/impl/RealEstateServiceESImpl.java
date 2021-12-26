package com.example.mockproject.elastic.service.impl;

import com.example.mockproject.elastic.model.RealEstateModel;
import com.example.mockproject.elastic.repository.IRealEstateESRepository;
import com.example.mockproject.elastic.service.IRealEstateServiceES;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RealEstateServiceESImpl implements IRealEstateServiceES {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealEstateServiceESImpl.class);

    private static final String RealEstate_INDEX = "realestateindex";
    private final IRealEstateESRepository repository;


    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public RealEstateServiceESImpl(IRealEstateESRepository repository, ElasticsearchOperations elasticsearchOperations) {
        this.repository = repository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    @JmsListener(destination = "inbound.queue")
    public void createProductIndexBulk(final Message jsonMessage) {

        List<RealEstateModel> realEstateModels = this.convertJsonToObject(jsonMessage);

        List<IndexQuery> queries = realEstateModels.stream()
                .map(realEstateModel ->
                        new IndexQueryBuilder()
                                .withId(realEstateModel.getId())
                                .withObject(realEstateModel).build())
                .collect(Collectors.toList());

        try {
            String result = String.valueOf(elasticsearchOperations
                    .bulkIndex(queries, IndexCoordinates.of(RealEstate_INDEX)));
            LOGGER.info("Query save" + result);
        } catch (ElasticsearchException e) {
            LOGGER.error("Some issue with Elastic Search save queries " + e.getMessage());
        }
    }

    public List<RealEstateModel> processSearch(final String query) {
        LOGGER.info("Search with query {}", query);
        List<RealEstateModel> productMatches = new ArrayList<RealEstateModel>();
        // 1. Create query on multiple fields enabling fuzzy search
        try {
            QueryBuilder queryBuilder =
                    QueryBuilders
                            .multiMatchQuery(query, "title", "address")
                            .fuzziness(Fuzziness.AUTO);

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withFilter(queryBuilder)
                    .build();

            // 2. Execute search
            SearchHits<RealEstateModel> productHits =
                    elasticsearchOperations
                            .search(searchQuery, RealEstateModel.class,
                                    IndexCoordinates.of(RealEstate_INDEX));

            // 3. Map searchHits to product list
            productHits.forEach(searchHit -> {
                productMatches.add(searchHit.getContent());
            });

        } catch (ElasticsearchException e) {
            LOGGER.error("Error process searching " + e.getMessage());
        }

        return productMatches;
    }

    private List<RealEstateModel> convertJsonToObject(final Message message) {
        List<RealEstateModel> realEstateModels = new ArrayList<>();
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String text = null;
            try {
                text = textMessage.getText();
                LOGGER.info("Received: text----- " + text);
                Gson gson = new Gson();
                Type userListType = new TypeToken<ArrayList<RealEstateModel>>() {
                }.getType();

                realEstateModels = gson.fromJson(text, userListType);
            } catch (JMSException e) {
                LOGGER.error(e.getMessage(), "ERROR get Text JMS Message");
            }
        } else {
            LOGGER.info("Received: json  ----" + message);
        }

        return realEstateModels;
    }
}
