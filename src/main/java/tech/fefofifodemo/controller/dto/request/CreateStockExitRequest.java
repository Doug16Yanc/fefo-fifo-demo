package tech.fefofifodemo.controller.dto.request;

import tech.fefofifodemo.domain.enums.ExitReason;

import java.time.LocalDate;

public record CreateStockExitRequest(
        LocalDate exitDate,
        int quantity,
        ExitReason exitReason
) {
}
