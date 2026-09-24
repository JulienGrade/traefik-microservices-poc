package fr.juliengrade.poc;

import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class RoutingIsolationIT {
    @Test void doesNotExposeAdminHealthOrLookalikePaths() {
        await("Gateway prête", () -> get("/api/products").status() == 200);
        for (String path : new String[]{"/api/products-evil", "/api/orders-evil", "/actuator/health", "/api/http/routers"}) {
            assertThat(get(path).status()).as(path).isEqualTo(404);
        }
    }
}
