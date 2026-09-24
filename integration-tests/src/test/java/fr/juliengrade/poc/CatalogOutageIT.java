package fr.juliengrade.poc;

import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class CatalogOutageIT {
    @Test void returns503WhenTheEntireCatalogIsUnavailable() {
        await("Commandes prêtes", () -> get("/api/orders/instance").status() == 200);
        try {
            compose("stop", "product-service");
            var response = post("/api/orders", "{\"productId\":1,\"quantity\":2}");
            assertThat(response.status()).isEqualTo(503);
        } finally {
            scale(1);
        }
    }
}
