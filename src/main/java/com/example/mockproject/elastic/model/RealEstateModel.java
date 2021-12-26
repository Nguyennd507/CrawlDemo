package com.example.mockproject.elastic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "realestateindex")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealEstateModel {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "area")
    private String area;

    @Field(type = FieldType.Text, name = "contact")
    private String contact;

    @Field(type = FieldType.Text, name = "price")
    private String price;

    @Field(type = FieldType.Text, name = "address")
    private String address;
}
