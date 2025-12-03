package cl.levelup.shop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonBackReference
    private Categoria categoria;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;
    
    @Column(length = 100)
    private String marca;
    
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(length = 255)
    private String imagen;
    
    @Column(columnDefinition = "TEXT")
    private String specs;
    
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CarritoItem> carritoItems = new ArrayList<>();
    
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PedidoItem> pedidoItems = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (stock == null) {
            stock = 0;
        }
        if (activo == null) {
            activo = true;
        }
    }
}
