package com.example.mockproject.crawler.helper;

import com.example.mockproject.crawler.dto.RealEstateCrawlDTO;
import com.example.mockproject.crawler.service.ScraperService;
import com.example.mockproject.exception.ScraperServiceException;
import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class GrabManager {

    public static final int THREAD_COUNT = 5;
    public static final String BASE_URL = "https://batdongsan24h.com.vn/ban-biet-thu-villas-tai-viet-nam-s45845/-1/-1/-1?page=%d";
    private static final Logger LOGGER = LoggerFactory.getLogger(GrabManager.class);
    private static final long PAUSE_TIME = 1000;
    private static final String PATH_TOTAL_PAGE = "src/main/resources/static/totalPage.txt";

    private final Set<String> masterList = new HashSet<>();
    private final List<Future<Set<RealEstateCrawlDTO>>> futures = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    private int count = 0;
    private int totalPages;
    private Set<String> links;
    private boolean isLastPage = false;

    @Autowired
    private ScraperService service;

    public GrabManager() {
    }

    public void go() throws InterruptedException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        links = HandleFile.readFile("src/main/resources/static/latestLink.txt");
        int totalPages = totalPages();
        int latestPage = HandleFile.readFileTotal(PATH_TOTAL_PAGE);
        int currentPageCrawl = totalPages() - latestPage + 1;

        if (currentPageCrawl > 11) currentPageCrawl = 11;

        for (int i = 1; i <= currentPageCrawl; i++) {

            if (i == currentPageCrawl) {
                isLastPage = true;
            }
            submitNewURL(String.format(BASE_URL, i));
        }
        while (checkPageGrabs()) ;
        stopWatch.stop();

        LOGGER.info("write latestPage", totalPages);
        HandleFile.writeFileTotal(PATH_TOTAL_PAGE, totalPages);

        System.out.println("Found " + masterList.size() + " urls");
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");

    }

    private boolean checkPageGrabs() throws InterruptedException {
        Thread.sleep(PAUSE_TIME);
        Set<RealEstateCrawlDTO> pageSet = new HashSet<>();
        Iterator<Future<Set<RealEstateCrawlDTO>>> iterator = futures.iterator();
        while (iterator.hasNext()) {
            Future<Set<RealEstateCrawlDTO>> future = iterator.next();
            if (future.isDone()) {
                iterator.remove();
                try {
                    count++;
                    pageSet.addAll(future.get());
                    if (count >= 10 || !iterator.hasNext()) {
                        String saveType = !iterator.hasNext() ? "END PAGE" : "10 PAGES";

                        LOGGER.info(String.format("Save DB %s : %d", saveType, pageSet.size()));
                        service.saveSetRealEstates(pageSet);
                        pageSet = new HashSet<>();
                        count = 0;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Save Data Crawl Repo" + e.getMessage());
                } catch (ScraperServiceException e) {
                    LOGGER.error("Save data service" + e.getMessage());
                }
            }
        }

        return (futures.size() > 0);
    }


    private void submitNewURL(String url) {
        masterList.add(url);
        GrabPage grabPage = new GrabPage(url, links, isLastPage);
        Future<Set<RealEstateCrawlDTO>> future = executorService.submit(grabPage);
        futures.add(future);
    }

    private int totalPages() {
        try {
            Document document = Jsoup.connect("https://batdongsan24h.com.vn/ban-biet-thu-villas-tai-viet-nam-s45845")
                    .userAgent("Chrome")
                    .get();
            Element element = document.getElementById("center-body");
            Elements elements = element.getElementsByClass("item-re-list clearfix");
            String[] urlPart = element.getElementsByClass("pagination")
                    .select("a").last().attr("href")
                    .split("=");

            if (urlPart.length < 2) {
                return 0;
            }
            totalPages = Integer.parseInt(urlPart[1]);
        } catch (IOException ex) {
            LOGGER.error("Error when access get total page " + ex.getMessage());
            ex.printStackTrace();
        }

        return totalPages;
    }
}
