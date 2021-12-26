package com.example.mockproject;

import com.example.mockproject.crawler.service.CrawlCronJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableJms
public class MockProjectApplication {

    @Autowired
    private CrawlCronJobService service;

    public static void main(String[] args) {
        SpringApplication.run(MockProjectApplication.class, args);

    }

    @PostConstruct
    public void execute() throws IOException, InterruptedException {
        service.executeCrawl();
    }
}
