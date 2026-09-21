package fr.juliengrade.order;

import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductClient {
    private final RestClient http;

    public ProductClient(RestClient.Builder builder,
                         @Value("${catalog.url}") String url) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(2));
        http = builder.baseUrl(url).requestFactory(factory).build();
    }

    public CatalogProduct find(long id) {
        try {
            var product = http.get().uri("/products/{id}", id).retrieve().body(CatalogProduct.class);
            if (product == null || product.price() == null || product.id() != id) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Réponse catalogue invalide");
            }
            return product;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produit introuvable");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catalogue indisponible");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catalogue indisponible");
        }
    }

}
