package cl.levelup.shop.dto.response;

import cl.levelup.shop.entity.enums.Rol;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String email,
        String nombre,
        String apellido,
        String telefono,
        Boolean activo,
        Rol rol,
        LocalDateTime fechaRegistro
) {
}
