package tech.fefofifodemo.controller.dto.response;

import tech.fefofifodemo.domain.enums.BatchStatus;

import java.time.LocalDate;

public record BatchResponse(
        Long id,
        Long batchNumber,
        LocalDate manufacturingDate,
        LocalDate expirationDate,
        int initialQuantity,
        int currentQuantity,
        BatchStatus batchStatus
) {
}
