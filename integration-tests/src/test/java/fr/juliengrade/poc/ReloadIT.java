package fr.juliengrade.poc;

import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class ReloadIT {
    @Test void reloadsTheMiddlewareWithoutRecreatingTraefik() throws Exception {
        var path = ROOT.resolve("traefik/dynamic.yaml");
        String original = Files.readString(path);
        String before = gateway();
        try {
            Files.writeString(path, original.replace("X-Gateway: traefik", "X-Gateway: traefik-reloaded"));
            await("Middleware rechargé", () -> get("/api/orders/instance").headers()
                    .firstValue("X-Gateway").orElse("").equals("traefik-reloaded"));
            assertThat(gateway()).isEqualTo(before);
        } finally {
            Files.writeString(path, original);
        }
        await("Middleware restauré", () -> get("/api/orders/instance").headers()
                .firstValue("X-Gateway").orElse("").equals("traefik"));
    }
}
