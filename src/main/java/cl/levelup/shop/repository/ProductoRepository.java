package cl.levelup.shop.repository;

import cl.levelup.shop.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findByCode(String code);
    
    List<Producto> findByCategoriaId(String categoriaId);
    
    List<Producto> findByActivoTrue();
    
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.categoria.id = :categoriaId")
    List<Producto> findActivosByCategoriaId(@Param("categoriaId") String categoriaId);
    
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Producto> buscarProductos(@Param("keyword") String keyword);
}
