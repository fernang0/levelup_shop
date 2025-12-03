package cl.levelup.shop.controller;

import cl.levelup.shop.dto.response.CategoriaResponseDTO;
import cl.levelup.shop.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Endpoints para gestión de categorías de productos")
public class CategoriaController {
    
    private final CategoriaService categoriaService;
    
    @GetMapping
    @Operation(summary = "Listar todas las categorías", description = "Obtiene el listado completo de categorías disponibles")
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Obtiene los detalles de una categoría específica")
    public ResponseEntity<CategoriaResponseDTO> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }
}
