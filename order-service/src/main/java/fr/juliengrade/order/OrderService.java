package fr.juliengrade.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    private final ProductClient catalog;
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    public OrderService(ProductClient catalog) { this.catalog = catalog; }

    public Order create(CreateOrder request) {
        var product = catalog.find(request.productId());
        var order = new Order(UUID.randomUUID(), product.id(), product.name(), request.quantity(),
                product.price(), product.price().multiply(BigDecimal.valueOf(request.quantity())), Instant.now());
        orders.put(order.id(), order);
        return order;
    }

}
