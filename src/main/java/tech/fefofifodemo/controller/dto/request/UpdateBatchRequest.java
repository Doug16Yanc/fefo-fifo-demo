package tech.fefofifodemo.controller.dto.request;

import java.time.LocalDate;

public record UpdateBatchRequest(
        Long batchNumber,
        LocalDate manufacturingDate,
        LocalDate expirationDate,
        int initialQuantity
) {
}
