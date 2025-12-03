package cl.levelup.shop.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductoRequestDTO(
        @NotBlank(message = "El código del producto es requerido")
        @Size(max = 20, message = "El código no puede exceder 20 caracteres")
        String code,
        
        @NotBlank(message = "El nombre del producto es requerido")
        @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
        String nombre,
        
        @NotBlank(message = "La categoría es requerida")
        String categoriaId,
        
        @NotNull(message = "El precio es requerido")
        @Positive(message = "El precio debe ser positivo")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal precio,
        
        @NotNull(message = "El stock es requerido")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,
        
        @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
        String marca,
        
        @DecimalMin(value = "0.0", message = "El rating no puede ser negativo")
        @DecimalMax(value = "5.0", message = "El rating no puede ser mayor a 5")
        BigDecimal rating,
        
        String descripcion,
        
        @Size(max = 255, message = "La URL de la imagen no puede exceder 255 caracteres")
        String imagen,
        
        String specs,
        
        String tags,
        
        Boolean activo
) {
}
