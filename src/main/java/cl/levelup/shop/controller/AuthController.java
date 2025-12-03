package cl.levelup.shop.controller;

import cl.levelup.shop.dto.request.LoginRequestDTO;
import cl.levelup.shop.dto.request.RegisterRequestDTO;
import cl.levelup.shop.dto.response.AuthResponseDTO;
import cl.levelup.shop.dto.response.UsuarioResponseDTO;
import cl.levelup.shop.security.JwtUtil;
import cl.levelup.shop.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de usuarios")
public class AuthController {
    
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta de usuario y retorna el token JWT")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna el token JWT")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = usuarioService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Obtiene los datos del usuario autenticado")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioActual(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remover "Bearer "
        String email = jwtUtil.extractUsername(token);
        UsuarioResponseDTO usuario = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
}
