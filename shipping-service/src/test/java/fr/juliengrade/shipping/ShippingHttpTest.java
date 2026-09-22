package fr.juliengrade.shipping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShippingHttpTest {
    @Autowired TestRestTemplate http;
    @Test void returnsDeliveryQuote() {
        var response = http.getForEntity("/shipping/quote", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("4.90", "EUR", "deliveryDays");
    }
}
