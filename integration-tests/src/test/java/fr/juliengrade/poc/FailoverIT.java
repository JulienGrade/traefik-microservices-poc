package fr.juliengrade.poc;

import java.util.HashSet;
import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class FailoverIT {
    @Test void removesAStoppedReplicaAfterConvergence() {
        String before = gateway();
        try {
            scale(2);
            String[] ids = compose("ps", "-q", "product-service").split("\\R");
            assertThat(ids).hasSize(2);
            String victim = ids[0];
            String hostname = docker("inspect", "--format", "{{.Config.Hostname}}", victim);
            docker("stop", victim);
            pause(6000);
            var seen = new HashSet<String>();
            for (int i = 0; i < 20; i++) {
                var response = get("/api/products/instance");
                assertThat(response.status()).isEqualTo(200);
                seen.add(response.body().get("instance").asText());
                pause(200);
            }
            assertThat(seen).hasSize(1).doesNotContain(hostname);
            assertThat(gateway()).isEqualTo(before);
        } finally {
            scale(1);
        }
    }
}
