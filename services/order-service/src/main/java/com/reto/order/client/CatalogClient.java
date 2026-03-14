package com.reto.order.client;

import com.reto.order.dto.StockCheckResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogClient {

    private final RestTemplate restTemplate;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    public CatalogClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public StockCheckResponse checkStock(Long productId, Integer cantidad, String correlationId) {
        String url = catalogServiceUrl + "/productos/" + productId + "/check-stock?cantidad=" + cantidad;

        HttpHeaders headers = new HttpHeaders();
        if (correlationId != null && !correlationId.isBlank()) {
            headers.set("CorrelationId", correlationId);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<StockCheckResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                StockCheckResponse.class
        );

        return response.getBody();
    }
}