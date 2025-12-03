package cl.levelup.shop.service;

import cl.levelup.shop.dto.request.ProductoRequestDTO;
import cl.levelup.shop.dto.response.ProductoResponseDTO;
import cl.levelup.shop.entity.Categoria;
import cl.levelup.shop.entity.Producto;
import cl.levelup.shop.exception.BadRequestException;
import cl.levelup.shop.exception.InsufficientStockException;
import cl.levelup.shop.exception.ResourceNotFoundException;
import cl.levelup.shop.repository.CategoriaRepository;
import cl.levelup.shop.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        return convertirAResponse(producto);
    }
    
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorCode(String code) {
        Producto producto = productoRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + code));
        return convertirAResponse(producto);
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerPorCategoria(String categoriaId) {
        return productoRepository.findActivosByCategoriaId(categoriaId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscar(String keyword) {
        return productoRepository.buscarProductos(keyword).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + request.categoriaId()));
        
        if (productoRepository.findByCode(request.code()).isPresent()) {
            throw new BadRequestException("Ya existe un producto con el código: " + request.code());
        }
        
        Producto producto = Producto.builder()
                .code(request.code())
                .nombre(request.nombre())
                .categoria(categoria)
                .precio(request.precio())
                .stock(request.stock())
                .marca(request.marca())
                .rating(request.rating())
                .descripcion(request.descripcion())
                .imagen(request.imagen())
                .specs(request.specs())
                .tags(request.tags())
                .activo(request.activo() != null ? request.activo() : true)
                .build();
        
        Producto guardado = productoRepository.save(producto);
        return convertirAResponse(guardado);
    }
    
    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + request.categoriaId()));
        
        producto.setCode(request.code());
        producto.setNombre(request.nombre());
        producto.setCategoria(categoria);
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setMarca(request.marca());
        producto.setRating(request.rating());
        producto.setDescripcion(request.descripcion());
        producto.setImagen(request.imagen());
        producto.setSpecs(request.specs());
        producto.setTags(request.tags());
        if (request.activo() != null) {
            producto.setActivo(request.activo());
        }
        
        Producto actualizado = productoRepository.save(producto);
        return convertirAResponse(actualizado);
    }
    
    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }
    
    @Transactional
    public void actualizarStock(Long id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new InsufficientStockException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }
    
    private ProductoResponseDTO convertirAResponse(Producto producto) {
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getCode(),
                producto.getNombre(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getMarca(),
                producto.getRating(),
                producto.getDescripcion(),
                producto.getImagen(),
                producto.getSpecs(),
                producto.getTags(),
                producto.getActivo()
        );
    }
}
