package cl.levelup.shop.service;

import cl.levelup.shop.dto.request.PedidoRequestDTO;
import cl.levelup.shop.dto.response.PedidoItemResponseDTO;
import cl.levelup.shop.dto.response.PedidoResponseDTO;
import cl.levelup.shop.entity.*;
import cl.levelup.shop.entity.enums.EstadoCarrito;
import cl.levelup.shop.entity.enums.EstadoPedido;
import cl.levelup.shop.exception.BadRequestException;
import cl.levelup.shop.exception.InsufficientStockException;
import cl.levelup.shop.exception.ResourceNotFoundException;
import cl.levelup.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoService productoService;
    
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
        return convertirAResponse(pedido);
    }
    
    @Transactional
    public PedidoResponseDTO crearDesdeCarrito(Long usuarioId, String direccionEnvio) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usuarioId));
        
        // Buscar carrito activo
        Carrito carrito = carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new BadRequestException("No hay carrito activo para el usuario"));
        
        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());
        
        if (items.isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }
        
        // Validar stock y calcular total
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            if (!producto.getActivo()) {
                throw new BadRequestException("El producto " + producto.getNombre() + " no está disponible");
            }
            if (producto.getStock() < item.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente para " + producto.getNombre());
            }
            BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }
        
        // Crear pedido
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .total(total)
                .estado(EstadoPedido.PENDIENTE)
                .direccionEnvio(direccionEnvio)
                .build();
        
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        // Crear items del pedido y actualizar stock
        for (CarritoItem carritoItem : items) {
            PedidoItem pedidoItem = PedidoItem.builder()
                    .pedido(pedidoGuardado)
                    .producto(carritoItem.getProducto())
                    .cantidad(carritoItem.getCantidad())
                    .precioUnitario(carritoItem.getPrecioUnitario())
                    .subtotal(carritoItem.getPrecioUnitario().multiply(BigDecimal.valueOf(carritoItem.getCantidad())))
                    .build();
            
            pedidoItemRepository.save(pedidoItem);
            
            // Descontar stock
            productoService.actualizarStock(carritoItem.getProducto().getId(), -carritoItem.getCantidad());
        }
        
        // Marcar carrito como comprado
        carrito.setEstado(EstadoCarrito.COMPRADO);
        carritoRepository.save(carrito);
        
        return convertirAResponse(pedidoGuardado);
    }
    
    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + request.usuarioId()));
        
        if (request.items().isEmpty()) {
            throw new BadRequestException("El pedido debe tener al menos un item");
        }
        
        // Validar y calcular total
        BigDecimal total = BigDecimal.ZERO;
        for (var itemRequest : request.items()) {
            Producto producto = productoRepository.findById(itemRequest.productoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + itemRequest.productoId()));
            
            if (!producto.getActivo()) {
                throw new BadRequestException("El producto " + producto.getNombre() + " no está disponible");
            }
            if (producto.getStock() < itemRequest.cantidad()) {
                throw new InsufficientStockException("Stock insuficiente para " + producto.getNombre());
            }
            
            BigDecimal subtotal = itemRequest.precioUnitario().multiply(BigDecimal.valueOf(itemRequest.cantidad()));
            total = total.add(subtotal);
        }
        
        // Crear pedido
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .total(total)
                .estado(EstadoPedido.PENDIENTE)
                .direccionEnvio(request.direccionEnvio())
                .build();
        
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        // Crear items del pedido
        for (var itemRequest : request.items()) {
            Producto producto = productoRepository.findById(itemRequest.productoId()).get();
            
            PedidoItem pedidoItem = PedidoItem.builder()
                    .pedido(pedidoGuardado)
                    .producto(producto)
                    .cantidad(itemRequest.cantidad())
                    .precioUnitario(itemRequest.precioUnitario())
                    .subtotal(itemRequest.precioUnitario().multiply(BigDecimal.valueOf(itemRequest.cantidad())))
                    .build();
            
            pedidoItemRepository.save(pedidoItem);
            
            // Descontar stock
            productoService.actualizarStock(producto.getId(), -itemRequest.cantidad());
        }
        
        return convertirAResponse(pedidoGuardado);
    }
    
    @Transactional
    public PedidoResponseDTO actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + id));
        
        // Si se cancela un pedido pagado, devolver stock
        if (pedido.getEstado() == EstadoPedido.PAGADO && nuevoEstado == EstadoPedido.CANCELADO) {
            List<PedidoItem> items = pedidoItemRepository.findByPedidoId(id);
            for (PedidoItem item : items) {
                productoService.actualizarStock(item.getProducto().getId(), item.getCantidad());
            }
        }
        
        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        return convertirAResponse(actualizado);
    }
    
    private PedidoResponseDTO convertirAResponse(Pedido pedido) {
        List<PedidoItem> items = pedidoItemRepository.findByPedidoId(pedido.getId());
        
        List<PedidoItemResponseDTO> itemsDTO = items.stream()
                .map(this::convertirItemAResponse)
                .collect(Collectors.toList());
        
        int totalItems = items.stream()
                .mapToInt(PedidoItem::getCantidad)
                .sum();
        
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
                pedido.getUsuario().getEmail(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getDireccionEnvio(),
                pedido.getFechaPedido(),
                itemsDTO,
                totalItems
        );
    }
    
    private PedidoItemResponseDTO convertirItemAResponse(PedidoItem item) {
        return new PedidoItemResponseDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getProducto().getCode(),
                item.getProducto().getImagen(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()
        );
    }
}
