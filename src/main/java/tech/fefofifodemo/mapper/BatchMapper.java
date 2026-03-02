package tech.fefofifodemo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import tech.fefofifodemo.controller.dto.request.*;
import tech.fefofifodemo.controller.dto.response.*;
import tech.fefofifodemo.domain.Batch;
import tech.fefofifodemo.domain.views.BatchFefo;
import tech.fefofifodemo.domain.views.BatchFifo;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentQuantity", source = "initialQuantity")
    @Mapping(target = "medicament", ignore = true)
    Batch toEntity(CreateBatchRequest request);

    void updateEntityFromDto(UpdateBatchRequest dto, @MappingTarget Batch entity);

    BatchResponse toResponse(Batch batch);

    BatchFefoResponse toFefoResponse(BatchFefo batchFefo);

    BatchFifoResponse toFifoResponse(BatchFifo batchFifo);

    default LocalDate getFirstEntryDate(Batch batch) {
        if (batch.getStockEntries() == null || batch.getStockEntries().isEmpty()) {
            return null;
        }
        return batch.getStockEntries().getFirst().getEntryDate();
    }
}