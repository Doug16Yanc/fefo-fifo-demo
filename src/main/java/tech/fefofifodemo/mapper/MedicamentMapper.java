package tech.fefofifodemo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.UpdateMedicamentRequest;
import tech.fefofifodemo.controller.dto.response.MedicamentResponse;
import tech.fefofifodemo.domain.Medicament;

@Mapper(componentModel = "spring")
public interface MedicamentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batches", ignore = true)
    Medicament toEntity(CreateMedicamentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "batches", ignore = true)
    void updateEntityFromDto(UpdateMedicamentRequest request, @MappingTarget Medicament medicament);

    MedicamentResponse toResponse(Medicament medicament);
}