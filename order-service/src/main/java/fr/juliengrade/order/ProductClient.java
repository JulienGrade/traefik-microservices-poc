package fr.juliengrade.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {
    private final RestClient http;

    public ProductClient(RestClient.Builder builder, @Value("${catalog.url}") String url) {
        http = builder.baseUrl(url).build();
    }

    public CatalogProduct find(long id) {
        return http.get().uri("/products/{id}", id).retrieve().body(CatalogProduct.class);
    }
}
