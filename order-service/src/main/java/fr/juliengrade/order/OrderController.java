package fr.juliengrade.order;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orders;

    public OrderController(OrderService orders) { this.orders = orders; }

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrder request) {
        var order = orders.create(request);
        return ResponseEntity.created(URI.create("/api/orders/" + order.id())).body(order);
    }

}
