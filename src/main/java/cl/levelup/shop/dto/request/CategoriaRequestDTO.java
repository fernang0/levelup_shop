package cl.levelup.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(
        @NotBlank(message = "El ID de la categoría es requerido")
        @Size(max = 10, message = "El ID no puede exceder 10 caracteres")
        String id,
        
        @NotBlank(message = "El nombre de la categoría es requerido")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre
) {
}
