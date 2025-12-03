package cl.levelup.shop.controller;

import cl.levelup.shop.dto.response.PedidoResponseDTO;
import cl.levelup.shop.entity.enums.EstadoPedido;
import cl.levelup.shop.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Admin - Pedidos", description = "Endpoints de administración de pedidos")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminPedidoController {
    
    private final PedidoService pedidoService;
    
    @GetMapping
    @Operation(summary = "Listar todos los pedidos", description = "Obtiene todos los pedidos del sistema (solo ADMIN)")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }
    
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar pedidos por estado", description = "Obtiene pedidos filtrados por estado (solo ADMIN)")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorEstado(@PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }
    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de pedido", description = "Actualiza el estado de un pedido (solo ADMIN)")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }
}
