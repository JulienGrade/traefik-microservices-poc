package fr.juliengrade.order;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Test void calculatesTotalFromTheCatalogPrice() {
        var client = mock(ProductClient.class);
        when(client.find(1)).thenReturn(new CatalogProduct(1, "Clavier", new BigDecimal("89.90")));
        var order = new OrderService(client).create(new CreateOrder(1, 2));
        assertThat(order.total()).isEqualByComparingTo("179.80");
        assertThat(order.id()).isNotNull();
        verify(client).find(1);
    }
}
