package fr.juliengrade.product;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class InstanceController {
    private final String instance;

    public InstanceController(@Value("${HOSTNAME:local-product}") String instance) { this.instance = instance; }

    @GetMapping("/products/instance")
    public Map<String, String> instance() { return Map.of("service", "product-service", "instance", instance); }
}
