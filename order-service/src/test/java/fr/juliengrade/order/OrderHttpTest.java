package fr.juliengrade.order;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderHttpTest {
    @Autowired TestRestTemplate http;
    @MockitoBean ProductClient catalog;

    @Test void createsAndReadsOrderWithServerSidePrice() {
        when(catalog.find(1)).thenReturn(new CatalogProduct(1, "Clavier", new BigDecimal("89.90")));
        var response = http.postForEntity("/orders", new CreateOrder(1, 2), Order.class);
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation().toString()).startsWith("/api/orders/");
        var order = response.getBody();
        assertThat(order.total()).isEqualByComparingTo("179.80");
        assertThat(http.getForObject("/orders/" + order.id(), Order.class)).isEqualTo(order);
    }

    @Test void rejectsInvalidQuantitiesBeforeCallingCatalog() {
        for (int quantity : new int[]{0, -1, 101}) {
            assertThat(http.postForEntity("/orders", new CreateOrder(1, quantity), String.class)
                    .getStatusCode().value()).isEqualTo(400);
        }
        verifyNoInteractions(catalog);
    }

    @Test void missingOrderReturns404() {
        assertThat(http.getForEntity("/orders/" + UUID.randomUUID(), String.class)
                .getStatusCode().value()).isEqualTo(404);
    }
}
