package com.example.mockproject.crawler.service.impl;

import com.example.mockproject.crawler.dto.RealEstateCrawlDTO;
import com.example.mockproject.crawler.entity.RealEstate;
import com.example.mockproject.crawler.repository.IRealEstateRepository;
import com.example.mockproject.crawler.service.ScraperService;
import com.example.mockproject.exception.ScraperServiceException;
import com.google.gson.Gson;
import org.hibernate.HibernateException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScraperServiceImpl implements ScraperService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScraperServiceImpl.class);

    private final IRealEstateRepository repository;
    private final ModelMapper mapper;
    private final JmsTemplate jmsTemplate;
    private Set<RealEstate> realEstateSet = new HashSet<>();

    @Autowired
    public ScraperServiceImpl(IRealEstateRepository repository, ModelMapper mapper, JmsTemplate jmsTemplate) {
        this.repository = repository;
        this.mapper = mapper;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void saveSetRealEstates(Set<RealEstateCrawlDTO> realEstateCrawlDTOSet) throws ScraperServiceException {
        Set<RealEstate> realEstateSet = convertSet(realEstateCrawlDTOSet);
        try {
            List<RealEstate> realEstates = repository.saveAll(realEstateSet);
            Gson gson = new Gson();
            String json = gson.toJson(realEstates);
            if (!realEstates.isEmpty()) {
                LOGGER.info("Send message to RealEstateServiceESImpl " + json);
                jmsTemplate.convertAndSend("inbound.queue", json);
            } else {
                LOGGER.info("No new Data to send message " + json);
            }

        } catch (HibernateException e) {
            LOGGER.error("Some issue save all saveSetRealEstates " + e.getMessage());
            throw new ScraperServiceException("Some issue save all saveSetRealEstates" + ScraperService.class.getName());

        } catch (Exception e) {
            LOGGER.error("saveSetRealEstates" + e.getMessage());
        }

    }
    
    private Set<RealEstate> convertSet(Set<RealEstateCrawlDTO> realEstateCrawlDTOSet) {
        realEstateSet = realEstateCrawlDTOSet.stream()
                .map(x -> mapper.map(x, RealEstate.class))
                .collect(Collectors.toSet());

        return realEstateSet;
    }
}
