package fr.juliengrade.product;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductCatalog {
    private final List<Product> products = List.of(
            new Product(1, "Clavier mécanique", new BigDecimal("89.90")),
            new Product(2, "Souris sans fil", new BigDecimal("39.90")),
            new Product(3, "Écran 27 pouces", new BigDecimal("249.00")));

    public List<Product> all() { return products; }

}
