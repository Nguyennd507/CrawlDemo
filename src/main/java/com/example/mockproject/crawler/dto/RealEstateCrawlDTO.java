package com.example.mockproject.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RealEstateCrawlDTO {

    private String id;
    private String title;
    private String area;
    private String contact;
    private String price;
    private String address;


}
