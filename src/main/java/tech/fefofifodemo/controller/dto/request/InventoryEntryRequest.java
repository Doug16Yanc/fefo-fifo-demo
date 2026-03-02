package tech.fefofifodemo.controller.dto.request;

import jakarta.validation.Valid;

public record InventoryEntryRequest(
        @Valid CreateMedicamentRequest medicament,
        @Valid CreateBatchRequest batch
) {}