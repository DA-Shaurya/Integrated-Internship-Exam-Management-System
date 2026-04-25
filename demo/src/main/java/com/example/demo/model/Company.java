package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "companies")
@Data
@org.hibernate.annotations.SQLRestriction("is_deleted = false")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int companyId;

    private String companyName;
    private String location;

    private boolean isDeleted = false;
}