package fr.juliengrade.poc;

import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class DiscoveryIT {
    @Test void addsAndRemovesARouteFromDockerEvents() {
        String before = gateway();
        compose("--profile", "discovery", "stop", "shipping-service");
        await("Route initialement absente", () -> get("/api/shipping/quote").status() == 404);
        try {
            compose("--profile", "discovery", "up", "--build", "-d", "--wait", "--wait-timeout", "180", "shipping-service");
            await("Route découverte", () -> get("/api/shipping/quote").status() == 200);
            assertThat(get("/api/shipping/quote").body().get("amount").decimalValue()).isEqualByComparingTo("4.90");
            assertThat(gateway()).isEqualTo(before);
        } finally {
            compose("--profile", "discovery", "stop", "shipping-service");
        }
        await("Route retirée", () -> get("/api/shipping/quote").status() == 404);
    }
}
