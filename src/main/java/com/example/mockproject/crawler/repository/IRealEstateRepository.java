package com.example.mockproject.crawler.repository;

import com.example.mockproject.crawler.entity.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRealEstateRepository extends JpaRepository<RealEstate, Long> {
}
