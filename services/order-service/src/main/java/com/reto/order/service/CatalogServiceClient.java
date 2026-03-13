package com.reto.order.service;

import com.reto.order.entity.CatalogProductDTO;
import com.reto.order.entity.ProductoNoEncontradoException;
import com.reto.order.entity.StockInsuficienteException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogServiceClient {

    private final RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${spring.external.catalog.url:http://catalog-service:8082/productos}")
    private String catalogServiceUrl;

    public CatalogServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Consulta un producto del Catalog Service
     * @param productoId ID del producto a consultar
     * @return CatalogProductDTO con la información del producto
     * @throws ProductoNoEncontradoException si el producto no existe o el servicio no responde
     */
    public CatalogProductDTO obtenerProducto(Long productoId) {
        try {
            CatalogProductDTO producto = restTemplate.getForObject(
                    catalogServiceUrl + "/{id}",
                    CatalogProductDTO.class,
                    productoId
            );

            if (producto == null) {
                throw new ProductoNoEncontradoException("Producto no encontrado: " + productoId);
            }

            return producto;
        } catch (RestClientException e) {
            throw new ProductoNoEncontradoException(
                    "Error al consultar Catalog Service para producto: " + productoId,
                    e
            );
        }
    }

    /**
     * Valida que un producto existe y tiene stock disponible
     * @param productoId ID del producto
     * @param cantidadRequerida cantidad que se intenta comprar
     * @return true si tiene stock suficiente
     * @throws ProductoNoEncontradoException si no existe
     * @throws StockInsuficienteException si no hay stock
     */
    public boolean validarStock(Long productoId, Integer cantidadRequerida) {
        CatalogProductDTO producto = obtenerProducto(productoId);

        if (!producto.getActivo()) {
            throw new ProductoNoEncontradoException("Producto no disponible: " + productoId);
        }

        if (producto.getStock() < cantidadRequerida) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para producto '" + producto.getTitulo() + "' " +
                    "(ISBN: " + producto.getIsbn() + "). " +
                    "Disponibles: " + producto.getStock() + ", solicitados: " + cantidadRequerida
            );
        }

        return true;
    }
}
