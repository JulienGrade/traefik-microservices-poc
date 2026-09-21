package fr.juliengrade.order;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class InstanceController {
    private final String instance;

    public InstanceController(@Value("${HOSTNAME:local-order}") String instance) { this.instance = instance; }

    @GetMapping("/orders/instance")
    public Map<String, String> instance() { return Map.of("service", "order-service", "instance", instance); }
}
