package cl.levelup.shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {
    
    @Id
    @Column(length = 10)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();
}
