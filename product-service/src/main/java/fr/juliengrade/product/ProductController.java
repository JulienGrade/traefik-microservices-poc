package fr.juliengrade.product;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductCatalog catalog;

    public ProductController(ProductCatalog catalog) { this.catalog = catalog; }

    @GetMapping public List<Product> all() { return catalog.all(); }
}
