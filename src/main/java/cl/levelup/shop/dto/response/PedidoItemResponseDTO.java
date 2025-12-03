package cl.levelup.shop.dto.response;

import java.math.BigDecimal;

public record PedidoItemResponseDTO(
        Long id,
        Long productoId,
        String productoNombre,
        String productoCode,
        String productoImagen,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
