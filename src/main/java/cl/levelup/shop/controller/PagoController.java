package cl.levelup.shop.controller;

import cl.levelup.shop.dto.TransbankCommitResponseDTO;
import cl.levelup.shop.dto.TransbankInitRequestDTO;
import cl.levelup.shop.dto.TransbankInitResponseDTO;
import cl.levelup.shop.service.TransbankService;
import cl.levelup.shop.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Endpoints para procesar pagos con Transbank")
public class PagoController {
    
    private final TransbankService transbankService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/iniciar")
    @Operation(
        summary = "Iniciar transacción de pago",
        description = "Crea una transacción en Transbank para pagar un pedido",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<TransbankInitResponseDTO> iniciarPago(
            @Valid @RequestBody TransbankInitRequestDTO request,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7); // Remover "Bearer "
        Long usuarioId = jwtUtil.extractUserId(token);
        TransbankInitResponseDTO response = transbankService.iniciarTransaccion(request.pedidoId(), usuarioId);
        return ResponseEntity.ok(response);
    }
    
    @RequestMapping(value = "/confirmar", method = {RequestMethod.POST, RequestMethod.GET})
    @Operation(
        summary = "Confirmar transacción de pago",
        description = "Endpoint que recibe la confirmación de Transbank después del pago"
    )
    public String confirmarPago(@RequestParam("token_ws") String token) {
        try {
            TransbankCommitResponseDTO response = transbankService.confirmarTransaccion(token);
            
            // Retornar HTML con el resultado
            if ("AUTHORIZED".equals(response.status())) {
                return "<html><body>" +
                       "<h1>✅ Pago Exitoso</h1>" +
                       "<p>Orden: " + response.buyOrder() + "</p>" +
                       "<p>Monto: $" + response.amount() + "</p>" +
                       "<p>Código autorización: " + response.authorizationCode() + "</p>" +
                       "<p>Tu pedido ha sido pagado correctamente.</p>" +
                       "<a href='https://dsy-1104-henriquez-orellana-git-henriquezev3-fernang0s-projects.vercel.app'>Volver a la tienda</a>" +
                       "</body></html>";
            } else {
                return "<html><body>" +
                       "<h1>❌ Pago Rechazado</h1>" +
                       "<p>Estado: " + response.status() + "</p>" +
                       "<a href='https://dsy-1104-henriquez-orellana-git-henriquezev3-fernang0s-projects.vercel.app'>Volver a la tienda</a>" +
                       "</body></html>";
            }
        } catch (Exception e) {
            return "<html><body>" +
                   "<h1>⚠️ Error en el pago</h1>" +
                   "<p>" + e.getMessage() + "</p>" +
                   "<a href='https://dsy-1104-henriquez-orellana-git-henriquezev3-fernang0s-projects.vercel.app'>Volver a la tienda</a>" +
                   "</body></html>";
        }
    }
    
    @GetMapping("/estado/{token}")
    @Operation(
        summary = "Obtener estado de transacción",
        description = "Consulta el estado de una transacción en Transbank",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<TransbankCommitResponseDTO> obtenerEstado(@PathVariable String token) {
        TransbankCommitResponseDTO response = transbankService.obtenerEstado(token);
        return ResponseEntity.ok(response);
    }
}
