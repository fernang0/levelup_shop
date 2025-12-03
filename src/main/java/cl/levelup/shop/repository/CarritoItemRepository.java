package cl.levelup.shop.repository;

import cl.levelup.shop.entity.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    List<CarritoItem> findByCarritoId(Long carritoId);
    
    Optional<CarritoItem> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
    
    void deleteByCarritoId(Long carritoId);
}
