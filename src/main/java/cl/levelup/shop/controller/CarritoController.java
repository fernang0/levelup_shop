package cl.levelup.shop.controller;

import cl.levelup.shop.dto.request.CarritoItemRequestDTO;
import cl.levelup.shop.dto.response.CarritoResponseDTO;
import cl.levelup.shop.security.JwtUtil;
import cl.levelup.shop.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carrito")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Endpoints para gestión del carrito de compras")
@SecurityRequirement(name = "Bearer Authentication")
public class CarritoController {
    
    private final CarritoService carritoService;
    private final JwtUtil jwtUtil;
    
    private Long obtenerUsuarioIdDelToken(String authHeader) {
        String token = authHeader.substring(7); // Remover "Bearer "
        return jwtUtil.extractUserId(token);
    }
    
    @GetMapping
    @Operation(summary = "Obtener carrito activo", description = "Obtiene el carrito activo del usuario con todos sus items")
    public ResponseEntity<CarritoResponseDTO> obtenerCarrito(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = obtenerUsuarioIdDelToken(authHeader);
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(usuarioId));
    }
    
    @PostMapping("/items")
    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito o incrementa la cantidad si ya existe")
    public ResponseEntity<CarritoResponseDTO> agregarProducto(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody CarritoItemRequestDTO request) {
        Long usuarioId = obtenerUsuarioIdDelToken(authHeader);
        return ResponseEntity.ok(carritoService.agregarProducto(usuarioId, request));
    }
    
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad de un item", description = "Modifica la cantidad de un producto en el carrito")
    public ResponseEntity<CarritoResponseDTO> actualizarCantidad(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long itemId,
            @RequestParam Integer cantidad
    ) {
        Long usuarioId = obtenerUsuarioIdDelToken(authHeader);
        return ResponseEntity.ok(carritoService.actualizarCantidad(usuarioId, itemId, cantidad));
    }
    
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Eliminar item del carrito", description = "Elimina un producto específico del carrito")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(@RequestHeader("Authorization") String authHeader, @PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioIdDelToken(authHeader);
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, itemId));
    }
    
    @DeleteMapping
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito")
    public ResponseEntity<Void> vaciarCarrito(@RequestHeader("Authorization") String authHeader) {
        Long usuarioId = obtenerUsuarioIdDelToken(authHeader);
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
