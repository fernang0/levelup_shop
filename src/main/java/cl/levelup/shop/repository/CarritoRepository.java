package cl.levelup.shop.repository;

import cl.levelup.shop.entity.Carrito;
import cl.levelup.shop.entity.enums.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, EstadoCarrito estado);
    
    List<Carrito> findByUsuarioId(Long usuarioId);
}
