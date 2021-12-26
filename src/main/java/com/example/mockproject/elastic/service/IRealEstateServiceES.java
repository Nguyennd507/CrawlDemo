package com.example.mockproject.elastic.service;

import com.example.mockproject.elastic.model.RealEstateModel;

import javax.jms.Message;
import java.util.List;

public interface IRealEstateServiceES {
    void createProductIndexBulk(final Message jsonMessage);

    List<RealEstateModel> processSearch(final String query);
}
