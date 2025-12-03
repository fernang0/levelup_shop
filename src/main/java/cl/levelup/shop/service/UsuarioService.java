package cl.levelup.shop.service;

import cl.levelup.shop.dto.request.LoginRequestDTO;
import cl.levelup.shop.dto.request.RegisterRequestDTO;
import cl.levelup.shop.dto.request.UsuarioRequestDTO;
import cl.levelup.shop.dto.response.AuthResponseDTO;
import cl.levelup.shop.dto.response.UsuarioResponseDTO;
import cl.levelup.shop.entity.Usuario;
import cl.levelup.shop.entity.enums.Rol;
import cl.levelup.shop.exception.BadRequestException;
import cl.levelup.shop.exception.ResourceNotFoundException;
import cl.levelup.shop.exception.UnauthorizedException;
import cl.levelup.shop.repository.UsuarioRepository;
import cl.levelup.shop.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponseDTO registrar(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BadRequestException("El email ya está registrado");
        }
        
        // SEGURIDAD: Los usuarios públicos siempre se crean con rol USER
        Usuario usuario = Usuario.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .apellido(request.apellido())
                .telefono(request.telefono())
                .activo(true)
                .rol(Rol.USER)
                .build();
        
        Usuario guardado = usuarioRepository.save(usuario);
        
        String token = jwtUtil.generateToken(guardado.getEmail(), guardado.getId(), guardado.getRol().name());
        
        return new AuthResponseDTO(token, convertirAResponse(guardado));
    }
    
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        
        if (!usuario.getActivo()) {
            throw new UnauthorizedException("Usuario inactivo");
        }
        
        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId(), usuario.getRol().name());
        
        return new AuthResponseDTO(token, convertirAResponse(usuario));
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        return convertirAResponse(usuario);
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return convertirAResponse(usuario);
    }
    
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        
        if (!usuario.getEmail().equals(request.email()) && 
            usuarioRepository.existsByEmail(request.email())) {
            throw new BadRequestException("El email ya está en uso");
        }
        
        usuario.setEmail(request.email());
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        
        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirAResponse(actualizado);
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
