package com.reto.catalog.controller;

import com.reto.catalog.dto.StockCheckResponse;
import com.reto.catalog.entity.CatalogEntity;
import com.reto.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // POST /catalog/productos
    @PostMapping
    public ResponseEntity<CatalogEntity> crear(@RequestBody CatalogEntity catalog) {
        return ResponseEntity.ok(catalogService.crear(catalog));
    }

    // GET /catalog/productos
    @GetMapping
    public ResponseEntity<List<CatalogEntity>> listar() {
        return ResponseEntity.ok(catalogService.listar());
    }

    // GET /catalog/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CatalogEntity> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.buscarPorId(id));
    }

    // PUT /catalog/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CatalogEntity> actualizar(
            @PathVariable Long id,
            @RequestBody CatalogEntity catalog) {
        return ResponseEntity.ok(catalogService.actualizar(id, catalog));
    }

    // DELETE /catalog/productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        catalogService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /catalog/productos/{id}/check-stock?cantidad=2
    @GetMapping("/{id}/check-stock")
    public ResponseEntity<StockCheckResponse> checkStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad,
            @RequestHeader(value = "CorrelationId", required = false) String correlationId) {

        System.out.println("CorrelationId recibido en catalog-service: " + correlationId);
        return ResponseEntity.ok(catalogService.checkStock(id, cantidad));
    }
}