package fr.juliengrade.product;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProductInstanceTest {
    @Test void identifiesTheRespondingReplica() {
        assertThat(new InstanceController("catalogue-a").instance())
                .containsEntry("instance", "catalogue-a").containsEntry("service", "product-service");
    }
}
