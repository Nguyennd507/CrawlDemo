package com.example.mockproject.crawler.service;

import com.example.mockproject.crawler.dto.RealEstateCrawlDTO;
import com.example.mockproject.exception.ScraperServiceException;

import java.util.Set;

public interface ScraperService {

    void saveSetRealEstates(Set<RealEstateCrawlDTO> realEstateCrawlDTOSet) throws ScraperServiceException;
}
