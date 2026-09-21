package fr.juliengrade.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record CreateOrder(@Positive long productId, @Min(1) @Max(100) int quantity) { }
