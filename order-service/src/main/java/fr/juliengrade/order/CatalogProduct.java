package fr.juliengrade.order;

import java.math.BigDecimal;

public record CatalogProduct(long id, String name, BigDecimal price) { }
