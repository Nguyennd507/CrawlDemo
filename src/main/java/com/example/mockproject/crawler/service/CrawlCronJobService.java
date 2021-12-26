package com.example.mockproject.crawler.service;

import com.example.mockproject.crawler.helper.GrabManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CrawlCronJobService {

    private final GrabManager grabManager;

    public CrawlCronJobService(GrabManager grabManager) {
        this.grabManager = grabManager;
    }

    @Scheduled(cron = "* * */1 * * *")
    public void executeCrawl() throws IOException, InterruptedException {
        grabManager.go();
    }
}
