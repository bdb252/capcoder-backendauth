package com.ccdweb.springboot.nutrition.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
public class FoodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String foodCode;

    @Column(nullable = false, length = 200)
    private String foodName;

    @Column(nullable = false)
    private Double energy;

    private String categoryLarge;
    private String categoryMedium;
    private String categorySmall;

    private String servingSizeStandard;
    private Double foodWeight;

    @Column(nullable = false)
    private Double protein;

    @Column(nullable = false)
    private Double sugars;

    private Double carbohydrates;
    private Double fat;

    private Double dietaryFiber;
    private Integer dietaryFiberIsMissing;

    private Double water;
    private Integer waterIsMissing;

    @Column(nullable = false)
    private Double sodium;

    @Column(nullable = false)
    private Double saturatedFat;

    private Double potassium;
    private Integer potassiumIsMissing;

    private Double vitaminD;
    private Integer vitaminDIsMissing;

    private Double iron;
    private Integer ironIsMissing;

    private Double thiamin;
    private Integer thiaminIsMissing;

    private Double niacin;
    private Integer niacinIsMissing;

    private Double vitaminA;
    private Integer vitaminAIsMissing;

    private Double vitaminC;
    private Integer vitaminCIsMissing;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
