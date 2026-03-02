package tech.fefofifodemo.controller.dto.response;

import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;

public record MedicamentResponse(
        Long id,
        String name,
        String description,
        MedicamentCategory medicamentCategory,
        UnitOfMeasure unitOfMeasure
) {
}
