package tech.fefofifodemo.controller.dto.request;

import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;

import java.math.BigDecimal;

public record UpdateMedicamentRequest(
        String name,
        String description,
        MedicamentCategory medicamentCategory,
        UnitOfMeasure unitOfMeasure,
        BigDecimal volume
) {
}
