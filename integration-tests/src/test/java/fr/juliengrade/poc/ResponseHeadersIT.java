package fr.juliengrade.poc;

import org.junit.jupiter.api.Test;
import static fr.juliengrade.poc.StackSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

class ResponseHeadersIT {
    @Test void appliesTheSharedChainToBothServices() {
        for (String path : new String[]{"/api/products/instance", "/api/orders/instance"}) {
            await("Route prête : " + path, () -> get(path).status() == 200);
            var response = get(path);
            assertThat(response.headers().firstValue("X-Gateway")).contains("traefik");
            assertThat(response.headers().firstValue("X-Content-Type-Options")).contains("nosniff");
        }
    }
}
