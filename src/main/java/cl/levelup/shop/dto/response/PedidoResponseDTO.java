package cl.levelup.shop.dto.response;

import cl.levelup.shop.entity.enums.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        String usuarioEmail,
        BigDecimal total,
        EstadoPedido estado,
        String direccionEnvio,
        LocalDateTime fechaPedido,
        List<PedidoItemResponseDTO> items,
        Integer totalItems
) {
}
