package cl.levelup.shop.entity;

import cl.levelup.shop.entity.enums.EstadoPedido;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    @Column(name = "direccion_envio", nullable = false, columnDefinition = "TEXT")
    private String direccionEnvio;
    
    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaPedido = LocalDateTime.now();
    
    @Column(name = "transbank_token", length = 255)
    private String transbankToken;
    
    @Column(name = "transbank_buy_order", length = 255)
    private String transbankBuyOrder;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "codigo_autorizacion", length = 20)
    private String codigoAutorizacion;
    
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoItem> items = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPedido.PENDIENTE;
        }
    }
}
