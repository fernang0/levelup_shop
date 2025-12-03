package cl.levelup.shop.dto.response;

import cl.levelup.shop.entity.enums.EstadoCarrito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CarritoResponseDTO(
        Long id,
        Long usuarioId,
        LocalDateTime fechaCreacion,
        EstadoCarrito estado,
        List<CarritoItemResponseDTO> items,
        BigDecimal total,
        Integer totalItems
) {
}
