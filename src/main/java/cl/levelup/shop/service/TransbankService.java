package cl.levelup.shop.service;

import cl.levelup.shop.config.TransbankConfig;
import cl.levelup.shop.dto.TransbankCommitResponseDTO;
import cl.levelup.shop.dto.TransbankInitResponseDTO;
import cl.levelup.shop.entity.Pedido;
import cl.levelup.shop.entity.enums.EstadoPedido;
import cl.levelup.shop.exception.BadRequestException;
import cl.levelup.shop.exception.ResourceNotFoundException;
import cl.levelup.shop.repository.PedidoRepository;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionStatusResponse;
import cl.transbank.webpay.common.WebpayOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransbankService {
    
    private final PedidoRepository pedidoRepository;
    private final TransbankConfig transbankConfig;
    
    @Value("${transbank.return-url}")
    private String returnUrl;
    
    private WebpayPlus.Transaction getTransactionInstance() {
        WebpayOptions options = new WebpayOptions(
            transbankConfig.getCommerceCode(),
            transbankConfig.getApiKey(),
            IntegrationType.TEST
        );
        return new WebpayPlus.Transaction(options);
    }
    
    @Transactional
    public TransbankInitResponseDTO iniciarTransaccion(Long pedidoId, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId));
        
        // Verificar que el pedido pertenece al usuario
        if (!pedido.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("El pedido no pertenece al usuario");
        }
        
        // Verificar que el pedido está en estado PENDIENTE
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BadRequestException("El pedido no está en estado PENDIENTE");
        }
        
        try {
            WebpayPlus.Transaction transaction = getTransactionInstance();
            
            // Crear la transacción en Transbank
            String buyOrder = "ORDER-" + pedidoId + "-" + System.currentTimeMillis();
            String sessionId = "SESSION-" + usuarioId + "-" + System.currentTimeMillis();
            double amount = pedido.getTotal().doubleValue();
            
            WebpayPlusTransactionCreateResponse response = transaction.create(
                buyOrder,
                sessionId,
                amount,
                returnUrl
            );
            
            // Guardar el token en el pedido
            pedido.setTransbankToken(response.getToken());
            pedido.setTransbankBuyOrder(buyOrder);
            pedidoRepository.save(pedido);
            
            return new TransbankInitResponseDTO(
                response.getToken(),
                response.getUrl()
            );
            
        } catch (Exception e) {
            throw new BadRequestException("Error al iniciar la transacción: " + e.getMessage());
        }
    }
    
    @Transactional
    public TransbankCommitResponseDTO confirmarTransaccion(String token) {
        try {
            WebpayPlus.Transaction transaction = getTransactionInstance();
            
            // Confirmar la transacción con Transbank
            WebpayPlusTransactionCommitResponse response = transaction.commit(token);
            
            // Buscar el pedido por el token
            Pedido pedido = pedidoRepository.findAll().stream()
                .filter(p -> token.equals(p.getTransbankToken()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado para el token: " + token));
            
            // Verificar si la transacción fue aprobada
            if ("AUTHORIZED".equals(response.getStatus())) {
                pedido.setEstado(EstadoPedido.PAGADO);
                pedido.setFechaPago(LocalDateTime.now());
                pedido.setCodigoAutorizacion(response.getAuthorizationCode());
                pedidoRepository.save(pedido);
            }
            
            return new TransbankCommitResponseDTO(
                response.getBuyOrder(),
                response.getSessionId(),
                response.getAmount(),
                response.getStatus(),
                response.getAuthorizationCode(),
                response.getPaymentTypeCode(),
                String.valueOf(response.getResponseCode()),
                LocalDateTime.now(),
                (int) response.getInstallmentsNumber(),
                response.getCardDetail() != null ? response.getCardDetail().getCardNumber() : null
            );
            
        } catch (Exception e) {
            throw new BadRequestException("Error al confirmar la transacción: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public TransbankCommitResponseDTO obtenerEstado(String token) {
        try {
            WebpayPlus.Transaction transaction = getTransactionInstance();
            WebpayPlusTransactionStatusResponse response = transaction.status(token);
            
            return new TransbankCommitResponseDTO(
                response.getBuyOrder(),
                response.getSessionId(),
                response.getAmount(),
                response.getStatus(),
                response.getAuthorizationCode(),
                response.getPaymentTypeCode(),
                String.valueOf(response.getResponseCode()),
                LocalDateTime.now(),
                (int) response.getInstallmentsNumber(),
                response.getCardDetail() != null ? response.getCardDetail().getCardNumber() : null
            );
            
        } catch (Exception e) {
            throw new BadRequestException("Error al obtener el estado de la transacción: " + e.getMessage());
        }
    }
}
