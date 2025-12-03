package cl.levelup.shop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransbankInitRequestDTO(
    @NotNull(message = "El ID del pedido es requerido")
    @Positive(message = "El ID del pedido debe ser positivo")
    Long pedidoId
) {}
