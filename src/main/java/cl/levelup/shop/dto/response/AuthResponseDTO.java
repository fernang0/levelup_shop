package cl.levelup.shop.dto.response;

public record AuthResponseDTO(
        String token,
        UsuarioResponseDTO usuario
) {
}
