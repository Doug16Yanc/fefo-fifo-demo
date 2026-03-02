package tech.fefofifodemo.controller.dto.response;

import tech.fefofifodemo.domain.enums.ExitReason;

import java.time.LocalDate;

public record StockExitResponse(
        Long id,
        Long batchId,
        LocalDate exitDate,
        ExitReason exitReason
) {
}
