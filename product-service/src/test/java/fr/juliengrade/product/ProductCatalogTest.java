package fr.juliengrade.product;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProductCatalogTest {
    @Test void exposesThreeProductsWithAnExactPrice() {
        var products = new ProductCatalog().all();
        assertThat(products).hasSize(3);
        assertThat(products.get(0).price()).isEqualByComparingTo("89.90");
    }
}
