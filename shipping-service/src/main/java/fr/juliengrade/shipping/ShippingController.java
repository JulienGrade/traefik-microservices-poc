package fr.juliengrade.shipping;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipping")
public class ShippingController {
    @GetMapping("/quote")
    public Map<String, Object> quote() {
        return Map.of("currency", "EUR", "amount", new BigDecimal("4.90"), "deliveryDays", 3);
    }
}
