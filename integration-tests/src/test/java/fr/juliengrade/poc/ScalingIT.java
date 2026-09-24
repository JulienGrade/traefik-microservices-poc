package fr.juliengrade.poc;

import java.util.HashSet;
import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class ScalingIT {
    @Test void routesTrafficToThreeDiscoveredReplicas() {
        String before = gateway();
        try {
            scale(3);
            var seen = new HashSet<String>();
            for (int i = 0; i < 60; i++) {
                var response = get("/api/products/instance");
                assertThat(response.status()).isEqualTo(200);
                seen.add(response.body().get("instance").asText());
                pause(200);
            }
            assertThat(seen).as("Hostnames distincts atteints par la gateway").hasSize(3);
            assertThat(gateway()).isEqualTo(before);
        } finally {
            scale(1);
        }
    }
}
