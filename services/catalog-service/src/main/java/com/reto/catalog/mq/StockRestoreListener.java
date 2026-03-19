package com.reto.catalog.mq;

import com.reto.catalog.config.RabbitMQConfig;
import com.reto.catalog.dto.OrderCancelledEvent;
import com.reto.catalog.service.CatalogService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockRestoreListener {

    private final CatalogService catalogService;

    public StockRestoreListener(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESTORE)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        System.out.println("Mensaje recibido en catalog-service para restaurar stock: " + 
                           "ProductoID: " + event.getProductId() + ", Cantidad: " + event.getCantidad());
        
        try {
            catalogService.restaurarStock(event.getProductId(), event.getCantidad());
            System.out.println("Stock restaurado correctamente para el producto " + event.getProductId());
        } catch (Exception e) {
            System.err.println("Error al restaurar stock para el producto " + event.getProductId() + ": " + e.getMessage());
        }
    }
}
