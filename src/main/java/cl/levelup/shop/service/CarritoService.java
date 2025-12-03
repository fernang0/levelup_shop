package cl.levelup.shop.service;

import cl.levelup.shop.dto.request.CarritoItemRequestDTO;
import cl.levelup.shop.dto.response.CarritoItemResponseDTO;
import cl.levelup.shop.dto.response.CarritoResponseDTO;
import cl.levelup.shop.entity.Carrito;
import cl.levelup.shop.entity.CarritoItem;
import cl.levelup.shop.entity.Producto;
import cl.levelup.shop.entity.Usuario;
import cl.levelup.shop.entity.enums.EstadoCarrito;
import cl.levelup.shop.exception.BadRequestException;
import cl.levelup.shop.exception.InsufficientStockException;
import cl.levelup.shop.exception.ResourceNotFoundException;
import cl.levelup.shop.repository.CarritoItemRepository;
import cl.levelup.shop.repository.CarritoRepository;
import cl.levelup.shop.repository.ProductoRepository;
import cl.levelup.shop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {
    
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional(readOnly = true)
    public CarritoResponseDTO obtenerCarritoActivo(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);
        return convertirAResponse(carrito);
    }
    
    @Transactional
    public CarritoResponseDTO agregarProducto(Long usuarioId, CarritoItemRequestDTO request) {
        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);
        
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + request.productoId()));
        
        if (!producto.getActivo()) {
            throw new BadRequestException("El producto no está disponible");
        }
        
        if (producto.getStock() < request.cantidad()) {
            throw new InsufficientStockException("Stock insuficiente. Disponible: " + producto.getStock());
        }
        
        // Buscar si ya existe el producto en el carrito
        CarritoItem itemExistente = carritoItemRepository
                .findByCarritoIdAndProductoId(carrito.getId(), request.productoId())
                .orElse(null);
        
        if (itemExistente != null) {
            // Actualizar cantidad
            int nuevaCantidad = itemExistente.getCantidad() + request.cantidad();
            if (producto.getStock() < nuevaCantidad) {
                throw new InsufficientStockException("Stock insuficiente. Disponible: " + producto.getStock());
            }
            itemExistente.setCantidad(nuevaCantidad);
            carritoItemRepository.save(itemExistente);
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem = CarritoItem.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(request.cantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();
            carritoItemRepository.save(nuevoItem);
        }
        
        return convertirAResponse(carrito);
    }
    
    @Transactional
    public CarritoResponseDTO actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad) {
        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);
        
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado: " + itemId));
        
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new BadRequestException("El item no pertenece al carrito del usuario");
        }
        
        if (cantidad <= 0) {
            carritoItemRepository.delete(item);
        } else {
            if (item.getProducto().getStock() < cantidad) {
                throw new InsufficientStockException("Stock insuficiente. Disponible: " + item.getProducto().getStock());
            }
            item.setCantidad(cantidad);
            carritoItemRepository.save(item);
        }
        
        return convertirAResponse(carrito);
    }
    
    @Transactional
    public CarritoResponseDTO eliminarItem(Long usuarioId, Long itemId) {
        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);
        
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado: " + itemId));
        
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new BadRequestException("El item no pertenece al carrito del usuario");
        }
        
        carritoItemRepository.delete(item);
        return convertirAResponse(carrito);
    }
    
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarritoActivo(usuarioId);
        carritoItemRepository.deleteByCarritoId(carrito.getId());
    }
    
    @Transactional
    public void marcarComoComprado(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carritoId));
        carrito.setEstado(EstadoCarrito.COMPRADO);
        carritoRepository.save(carrito);
    }
    
    private Carrito obtenerOCrearCarritoActivo(Long usuarioId) {
        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));
                    
                    Carrito nuevoCarrito = Carrito.builder()
                            .usuario(usuario)
                            .estado(EstadoCarrito.ACTIVO)
                            .build();
                    return carritoRepository.save(nuevoCarrito);
                });
    }
    
    private CarritoResponseDTO convertirAResponse(Carrito carrito) {
        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());
        
        List<CarritoItemResponseDTO> itemsDTO = items.stream()
                .map(this::convertirItemAResponse)
                .collect(Collectors.toList());
        
        BigDecimal total = items.stream()
                .map(item -> item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalItems = items.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
        
        return new CarritoResponseDTO(
                carrito.getId(),
                carrito.getUsuario().getId(),
                carrito.getFechaCreacion(),
                carrito.getEstado(),
                itemsDTO,
                total,
                totalItems
        );
    }
    
    private CarritoItemResponseDTO convertirItemAResponse(CarritoItem item) {
        BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
        
        return new CarritoItemResponseDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getProducto().getCode(),
                item.getProducto().getImagen(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                subtotal
        );
    }
}
