package com.reto.catalog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private CatalogEntity product;

    public ProductImageEntity() {}

    public ProductImageEntity(String imageUrl, CatalogEntity product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CatalogEntity getProduct() {
        return product;
    }

    public void setProduct(CatalogEntity product) {
        this.product = product;
    }
}