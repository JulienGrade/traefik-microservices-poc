package fr.juliengrade.poc;

import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class ApiContractsIT {
    @Test void servesProductsThroughTraefik() {
        await("Catalogue prêt", () -> get("/api/products").status() == 200);
        assertThat(get("/api/products").body()).hasSize(3);
        assertThat(get("/api/products/1").body().get("price").decimalValue()).isEqualByComparingTo("89.90");
        assertThat(get("/api/products/999").status()).isEqualTo(404);
    }

    @Test void createsAndReadsAValidatedOrder() {
        await("Commandes prêtes", () -> get("/api/orders/instance").status() == 200);
        var response = post("/api/orders", "{\"productId\":1,\"quantity\":2}");
        assertThat(response.status()).isEqualTo(201);
        assertThat(response.body().get("total").decimalValue()).isEqualByComparingTo("179.80");
        String location = response.headers().firstValue("Location").orElseThrow();
        assertThat(get(location).body().get("id")).isEqualTo(response.body().get("id"));
        assertThat(post("/api/orders", "{\"productId\":1,\"quantity\":0}").status()).isEqualTo(400);
        assertThat(post("/api/orders", "{\"productId\":999,\"quantity\":1}").status()).isEqualTo(404);
    }
}
