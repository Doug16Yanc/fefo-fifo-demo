package tech.fefofifodemo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.fefofifodemo.controller.dto.response.ExpirationAlertResponse;
import tech.fefofifodemo.domain.ExpirationAlert;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpirationAlertMapper {

    @Mapping(target = "batchNumber", source = "batch.batchNumber")
    ExpirationAlertResponse toResponse(ExpirationAlert entity);

    List<ExpirationAlertResponse> toResponseList(List<ExpirationAlert> entities);
}