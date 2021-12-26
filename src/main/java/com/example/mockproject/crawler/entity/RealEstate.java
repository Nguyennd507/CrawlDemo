package com.example.mockproject.crawler.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "realEstate")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RealEstate implements Serializable {
    
    private static final long serialVersionUID = -2749977649306134186L;

    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "area")
    private String area;

    @Column(name = "contact")
    private String contact;

    @Column(name = "price")
    private String price;

    @Column(name = "address")
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modificationDate;
}
