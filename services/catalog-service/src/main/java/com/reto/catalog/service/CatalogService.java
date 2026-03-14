package com.reto.catalog.service;

import com.reto.catalog.dto.StockCheckResponse;
import com.reto.catalog.entity.CatalogEntity;
import com.reto.catalog.repository.CatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    // Crear libro
    public CatalogEntity crear(CatalogEntity catalog) {
        return catalogRepository.save(catalog);
    }

    // Listar libros activos
    public List<CatalogEntity> listar() {
        return catalogRepository.findByActivoTrue();
    }

    // Buscar por id
    public CatalogEntity buscarPorId(Long id) {
        return catalogRepository.findById(id)
                .filter(CatalogEntity::isActivo)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
    }

    // Actualizar libro
    public CatalogEntity actualizar(Long id, CatalogEntity datos) {
        CatalogEntity existente = buscarPorId(id);

        existente.setTitulo(datos.getTitulo());
        existente.setAutor(datos.getAutor());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setIsbn(datos.getIsbn());

        return catalogRepository.save(existente);
    }

    // Borrado lógico
    public void eliminar(Long id) {
        CatalogEntity catalog = buscarPorId(id);
        catalog.setActivo(false);
        catalogRepository.save(catalog);
    }

    // Validar stock
    public StockCheckResponse checkStock(Long id, Integer cantidad) {
        CatalogEntity producto = buscarPorId(id);
        boolean disponible = producto.getStock() != null && producto.getStock() >= cantidad;

        return new StockCheckResponse(
                producto.getId(),
                cantidad,
                producto.getStock(),
                disponible
        );
    }
}