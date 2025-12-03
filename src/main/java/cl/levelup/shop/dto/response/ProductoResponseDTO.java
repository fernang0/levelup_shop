package cl.levelup.shop.dto.response;

import java.math.BigDecimal;

public record ProductoResponseDTO(
        Long id,
        String code,
        String nombre,
        String categoriaId,
        String categoriaNombre,
        BigDecimal precio,
        Integer stock,
        String marca,
        BigDecimal rating,
        String descripcion,
        String imagen,
        String specs,
        String tags,
        Boolean activo
) {
}
