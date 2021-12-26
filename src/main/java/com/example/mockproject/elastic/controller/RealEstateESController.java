package com.example.mockproject.elastic.controller;

import com.example.mockproject.elastic.model.RealEstateModel;
import com.example.mockproject.elastic.service.IRealEstateServiceES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RealEstateESController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RealEstateESController.class);

    private final IRealEstateServiceES realEstateServiceES;

    @Autowired
    public RealEstateESController(IRealEstateServiceES realEstateServiceES) {
        this.realEstateServiceES = realEstateServiceES;
    }

    @GetMapping("/search")
    public ResponseEntity<List<RealEstateModel>> searchES(@RequestParam(value = "", required = false) String query) {
        LOGGER.info("Searching----", query);
        List<RealEstateModel> realEstateModels = realEstateServiceES.processSearch(query);
        LOGGER.info("ResultSearch----", realEstateModels);
        return new ResponseEntity<>(realEstateModels, HttpStatus.OK);
    }
}
