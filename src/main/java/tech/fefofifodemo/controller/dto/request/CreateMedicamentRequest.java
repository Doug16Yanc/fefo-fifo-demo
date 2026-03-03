package tech.fefofifodemo.controller.dto.request;

import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;

import java.math.BigDecimal;

public record CreateMedicamentRequest(
        String name,
        String description,
        MedicamentCategory medicamentCategory,
        UnitOfMeasure unitOfMeasure,
        Boolean coldChain,
        BigDecimal volume
) {
}
