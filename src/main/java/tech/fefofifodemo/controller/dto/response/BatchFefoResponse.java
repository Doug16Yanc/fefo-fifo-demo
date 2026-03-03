package tech.fefofifodemo.controller.dto.response;


import java.math.BigDecimal;
import java.time.LocalDate;

public record BatchFefoResponse(
        Long batchId,
        Long batchNumber,
        LocalDate expirationDate,
        int currentQuantity,
        String batchStatus,
        String medicamentName,
        BigDecimal volume,
        String medicamentCategory
) {
}
