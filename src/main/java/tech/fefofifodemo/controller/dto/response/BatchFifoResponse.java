package tech.fefofifodemo.controller.dto.response;

import java.time.LocalDate;

public record BatchFifoResponse(
        Long id,
        Long batchNumber,
        LocalDate expirationDate,
        int currentQuantity,
        String batchStatus,
        String medicamentName,
        String medicamentCategory,
        LocalDate entryDate
) {
}
