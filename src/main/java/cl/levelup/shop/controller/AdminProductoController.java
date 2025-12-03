package cl.levelup.shop.controller;

import cl.levelup.shop.dto.request.ProductoRequestDTO;
import cl.levelup.shop.dto.response.ProductoResponseDTO;
import cl.levelup.shop.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/productos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Admin - Productos", description = "Endpoints de administración de productos")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminProductoController {
    
    private final ProductoService productoService;
    
    @PostMapping
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto (solo ADMIN)")
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
        ProductoResponseDTO producto = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente (solo ADMIN)")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO request) {
        ProductoResponseDTO producto = productoService.actualizar(id, request);
        return ResponseEntity.ok(producto);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto (solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock", description = "Actualiza el stock de un producto (solo ADMIN)")
    public ResponseEntity<Void> actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok().build();
    }
}
