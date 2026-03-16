package com.reto.order.entity;

import java.math.BigDecimal;

// DTO para recibir datos del Catalog Service
public class CatalogProductDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private String categoria;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;

    // Constructores
    public CatalogProductDTO() {}

    public CatalogProductDTO(Long id, String titulo, String autor, String isbn,
                            String categoria, String descripcion, BigDecimal precio,
                            Integer stock, Boolean activo) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.activo = activo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
