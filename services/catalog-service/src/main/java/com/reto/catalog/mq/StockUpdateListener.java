package com.reto.catalog.mq;

import com.reto.catalog.config.RabbitMQConfig;
import com.reto.catalog.dto.OrderCreatedEvent;
import com.reto.catalog.service.CatalogService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockUpdateListener {

    private final CatalogService catalogService;

    public StockUpdateListener(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_STOCK)
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("Mensaje recibido en catalog-service para descontar stock: " + 
                           "ProductoID: " + event.getProductId() + ", Cantidad: " + event.getCantidad());
        
        try {
            catalogService.descontarStock(event.getProductId(), event.getCantidad());
            System.out.println("Stock actualizado correctamente para el producto " + event.getProductId());
        } catch (Exception e) {
            System.err.println("Error al actualizar stock para el producto " + event.getProductId() + ": " + e.getMessage());
        }
    }
}
