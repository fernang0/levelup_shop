package cl.levelup.shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CarritoItemRequestDTO(
        @NotNull(message = "El ID del producto es requerido")
        Long productoId,
        
        @NotNull(message = "La cantidad es requerida")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad
) {
}
