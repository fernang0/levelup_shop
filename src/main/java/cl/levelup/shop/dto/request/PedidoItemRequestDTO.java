package cl.levelup.shop.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PedidoItemRequestDTO(
        @NotNull(message = "El ID del producto es requerido")
        Long productoId,
        
        @NotNull(message = "La cantidad es requerida")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad,
        
        @NotNull(message = "El precio unitario es requerido")
        @Positive(message = "El precio unitario debe ser positivo")
        @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
        BigDecimal precioUnitario
) {
}
