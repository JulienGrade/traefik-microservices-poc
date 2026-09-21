package fr.juliengrade.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Order(UUID id, long productId, String productName, int quantity,
                    BigDecimal unitPrice, BigDecimal total, Instant createdAt) { }
