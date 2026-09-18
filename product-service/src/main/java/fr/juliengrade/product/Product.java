package fr.juliengrade.product;

import java.math.BigDecimal;

public record Product(long id, String name, BigDecimal price) { }
