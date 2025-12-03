package cl.levelup.shop.controller;

import cl.levelup.shop.dto.request.CreateUsuarioRequestDTO;
import cl.levelup.shop.dto.response.UsuarioResponseDTO;
import cl.levelup.shop.entity.Usuario;
import cl.levelup.shop.entity.enums.Rol;
import cl.levelup.shop.exception.BadRequestException;
import cl.levelup.shop.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Admin - Usuarios", description = "Endpoints de administración de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminUsuarioController {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios del sistema (solo ADMIN)")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }
    
    @PostMapping
    @Transactional
    @Operation(summary = "Crear usuario con rol específico", description = "Permite crear usuarios con cualquier rol (solo ADMIN)")
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@Valid @RequestBody CreateUsuarioRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BadRequestException("El email ya está registrado");
        }
        
        Usuario usuario = Usuario.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .apellido(request.apellido())
                .telefono(request.telefono())
                .activo(true)
                .rol(request.rol())
                .build();
        
        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirAResponse(guardado));
    }
    
    @PatchMapping("/{id}/activar")
    @Transactional
    @Operation(summary = "Activar/desactivar usuario", description = "Cambia el estado activo de un usuario (solo ADMIN)")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstadoActivo(@PathVariable Long id, @RequestParam Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado: " + id));
        
        usuario.setActivo(activo);
        Usuario actualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(convertirAResponse(actualizado));
    }
    
    @PatchMapping("/{id}/rol")
    @Transactional
    @Operation(summary = "Cambiar rol de usuario", description = "Cambia el rol de un usuario (solo ADMIN)")
    public ResponseEntity<UsuarioResponseDTO> cambiarRol(@PathVariable Long id, @RequestParam Rol rol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado: " + id));
        
        usuario.setRol(rol);
        Usuario actualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(convertirAResponse(actualizado));
    }
    
    private UsuarioResponseDTO convertirAResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTelefono(),
                usuario.getActivo(),
                usuario.getRol(),
                usuario.getFechaRegistro()
        );
    }
}
