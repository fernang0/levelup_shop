package cl.levelup.shop.controller;

import cl.levelup.shop.dto.response.ProductoResponseDTO;
import cl.levelup.shop.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para consulta de productos")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene el listado de productos activos con filtros opcionales")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos(
            @Parameter(description = "ID de categoría para filtrar") @RequestParam(required = false) String categoria,
            @Parameter(description = "Palabra clave para búsqueda") @RequestParam(required = false) String search
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(productoService.buscar(search));
        }
        
        if (categoria != null && !categoria.trim().isEmpty()) {
            return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
        }
        
        return ResponseEntity.ok(productoService.obtenerActivos());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto específico")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }
    
    @GetMapping("/codigo/{code}")
    @Operation(summary = "Obtener producto por código", description = "Obtiene un producto usando su código único (ej: JM001)")
    public ResponseEntity<ProductoResponseDTO> obtenerPorCodigo(@PathVariable String code) {
        return ResponseEntity.ok(productoService.obtenerPorCode(code));
    }
    
    @GetMapping("/categoria/{categoriaCode}")
    @Operation(summary = "Obtener productos por categoría", description = "Filtra productos por código de categoría (AC, CG, CO, JM, MP, MS, PP, SG)")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerPorCategoria(@PathVariable String categoriaCode) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoriaCode));
    }
}
