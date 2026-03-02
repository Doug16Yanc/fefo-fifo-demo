package tech.fefofifodemo.controller.dto.request;

import tech.fefofifodemo.domain.enums.BatchStatus;

import java.time.LocalDate;

public record CreateBatchRequest(
        Long medicamentId,
        Long batchNumber,
        LocalDate manufacturingDate,
        LocalDate expirationDate,
        int initialQuantity,
        BatchStatus batchStatus
) {
}
