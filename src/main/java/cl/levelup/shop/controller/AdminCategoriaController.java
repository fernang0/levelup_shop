package cl.levelup.shop.controller;

import cl.levelup.shop.dto.request.CategoriaRequestDTO;
import cl.levelup.shop.dto.response.CategoriaResponseDTO;
import cl.levelup.shop.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categorias")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Admin - Categorías", description = "Endpoints de administración de categorías")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminCategoriaController {
    
    private final CategoriaService categoriaService;
    
    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría (solo ADMIN)")
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO categoria = categoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente (solo ADMIN)")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable String id, @Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO categoria = categoriaService.actualizar(id, request);
        return ResponseEntity.ok(categoria);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría (solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
