package com.example.mockproject.crawler.helper;

import com.example.mockproject.crawler.dto.RealEstateCrawlDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


public class GrabPage implements Callable<Set<RealEstateCrawlDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrabPage.class);

    private static final String PATH_LATEST_LINK = "src/main/resources/static/latestLink.txt";
    private static final String DOMAIN = "https://batdongsan24h.com.vn";

    private final String url;
    private final Set<String> links;
    private final boolean isLastPage;

    public GrabPage(String url, Set<String> links, boolean isLatest) {
        this.url = url;
        this.links = links;
        this.isLastPage = isLatest;
    }


    @Override
    public Set<RealEstateCrawlDTO> call() {
        Set<RealEstateCrawlDTO> dataCrawlSet = new HashSet<>();
      
        try {
            Document document = Jsoup.connect(url).userAgent("Chrome").get();
            Element element = document.getElementById("center-body");
            Elements elements = element.getElementsByClass("item-re-list clearfix");
            String[] page = url.split("=");

            int numberPage = Integer.parseInt(page[1]);


            for (Element ads : elements) {
                RealEstateCrawlDTO responseDTO = RealEstateCrawlDTO
                        .builder()
                        .title(ads.getElementsByClass("clearfix box-title-item").first().text())
                        .area(ads.getElementsByClass("box-info-list").select("strong").get(0).text())
                        .price(ads.getElementsByClass("box-info-list").select("strong").get(1).text())
                        .contact(ads.getElementsByClass("box-info-list").select("strong").get(2).text())
                        .address(ads.getElementsByClass("box-info-list").select("strong").get(3).text())
                        .id(DOMAIN + ads.getElementsByClass("clearfix box-title-item").select("a").attr("href"))
                        .build();

                dataCrawlSet.add(responseDTO);
            }

            LOGGER.info(dataCrawlSet.size() + "++++" + url);
            if (numberPage == 1) {
                handlePage1(dataCrawlSet);
            }

            if (isLastPage) {
                dataCrawlSet = handleLastPage(dataCrawlSet);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return dataCrawlSet;
    }

    private void handlePage1(Set<RealEstateCrawlDTO> dataCrawlSet) {

        Set<String> links = dataCrawlSet.stream()
                .map(RealEstateCrawlDTO::getId)
                .collect(Collectors.toSet());
        LOGGER.info("wrtie latestLinks");
        HandleFile.writeFile(PATH_LATEST_LINK, links);

    }

    private Set<RealEstateCrawlDTO> handleLastPage(Set<RealEstateCrawlDTO> dataCrawlSet) {
        dataCrawlSet = dataCrawlSet.stream()
                .filter(data -> {
                    return !links.contains(data.getId());
                })
                .collect(Collectors.toSet());
        return dataCrawlSet;
    }


}
