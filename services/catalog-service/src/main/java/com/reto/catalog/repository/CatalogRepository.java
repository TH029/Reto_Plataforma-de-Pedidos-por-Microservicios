package com.reto.catalog.repository;

import com.reto.catalog.entity.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

    // Solo libros activos (borrado lógico)
    List<CatalogEntity> findByActivoTrue();
}