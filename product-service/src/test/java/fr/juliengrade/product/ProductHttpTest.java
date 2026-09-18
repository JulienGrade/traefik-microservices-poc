package fr.juliengrade.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductHttpTest {
    @Autowired TestRestTemplate http;

    @Test void listsSeededProducts() {
        var response = http.getForEntity("/products", Product[].class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(3);
    }

    @Test void returnsExactPrice() {
        var product = http.getForObject("/products/1", Product.class);
        assertThat(product.price()).isEqualByComparingTo("89.90");
    }

    @Test void unknownProductReturns404() {
        assertThat(http.getForEntity("/products/999", String.class).getStatusCode().value()).isEqualTo(404);
    }
}
