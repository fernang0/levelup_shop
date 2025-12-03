package cl.levelup.shop.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PedidoRequestDTO(
        @NotNull(message = "El usuario es requerido")
        Long usuarioId,
        
        @NotBlank(message = "La dirección de envío es requerida")
        @Size(min = 10, max = 500, message = "La dirección debe tener entre 10 y 500 caracteres")
        String direccionEnvio,
        
        @NotEmpty(message = "El pedido debe tener al menos un item")
        @Valid
        List<PedidoItemRequestDTO> items
) {
}
