package cl.levelup.shop.dto;

import java.time.LocalDateTime;

public record TransbankCommitResponseDTO(
    String buyOrder,
    String sessionId,
    Double amount,
    String status,
    String authorizationCode,
    String paymentTypeCode,
    String responseCode,
    LocalDateTime transactionDate,
    Integer installmentsNumber,
    String cardDetail
) {}
