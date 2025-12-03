package cl.levelup.shop.repository;

import cl.levelup.shop.entity.Pedido;
import cl.levelup.shop.entity.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByUsuarioId(Long usuarioId);
    
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(Long usuarioId);
    
    List<Pedido> findByEstado(EstadoPedido estado);
}
