package tech.fefofifodemo.controller.dto.response;

import tech.fefofifodemo.domain.enums.ExpirationAlertStatus;

import java.time.LocalDate;

public record ExpirationAlertResponse(
        Long id,
        LocalDate alertDate,
        int daysUntilExpiration,
        ExpirationAlertStatus expirationAlertStatus,
        Long batchNumber
) {
}
