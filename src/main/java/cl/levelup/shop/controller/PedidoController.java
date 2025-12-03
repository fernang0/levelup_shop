package cl.levelup.shop.controller;

import cl.levelup.shop.dto.response.PedidoResponseDTO;
import cl.levelup.shop.security.JwtUtil;
import cl.levelup.shop.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para gestión de pedidos")
@SecurityRequirement(name = "Bearer Authentication")
public class PedidoController {
    
    private final PedidoService pedidoService;
    private final JwtUtil jwtUtil;
    
    private Long obtenerUsuarioIdDelToken(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
    
    @GetMapping
    @Operation(summary = "Listar pedidos del usuario", description = "Obtiene el historial de pedidos del usuario autenticado")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = obtenerUsuarioIdDelToken(authHeader);
        return ResponseEntity.ok(pedidoService.obtenerPorUsuario(usuarioId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de pedido", description = "Obtiene los detalles completos de un pedido específico")
    public ResponseEntity<PedidoResponseDTO> obtenerPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }
    
    @PostMapping
    @Operation(summary = "Crear pedido desde carrito", description = "Convierte el carrito activo en un pedido")
    public ResponseEntity<PedidoResponseDTO> crearPedido(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> request) {
        Long usuarioId = obtenerUsuarioIdDelToken(authHeader);
        String direccionEnvio = request.get("direccionEnvio");
        if (direccionEnvio == null) {
            direccionEnvio = request.get("direccion_envio");
        }
        
        if (direccionEnvio == null || direccionEnvio.trim().isEmpty()) {
            throw new cl.levelup.shop.exception.BadRequestException("La dirección de envío es requerida");
        }
        
        PedidoResponseDTO pedido = pedidoService.crearDesdeCarrito(usuarioId, direccionEnvio);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
}
