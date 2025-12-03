package cl.levelup.shop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "El email es requerido")
        @Email(message = "El email debe ser válido")
        @Size(max = 150, message = "El email no puede exceder 150 caracteres")
        String email,
        
        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
        String password,
        
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,
        
        @NotBlank(message = "El apellido es requerido")
        @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
        String apellido,
        
        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String telefono
) {
}
