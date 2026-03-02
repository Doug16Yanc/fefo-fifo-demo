package tech.fefofifodemo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.fefofifodemo.controller.dto.request.CreateStockExitRequest;
import tech.fefofifodemo.controller.dto.response.StockExitResponse;
import tech.fefofifodemo.domain.StockExit;

@Mapper(componentModel = "spring")
public interface StockExitMapper {

    @Mapping(target = "id", ignore = true)
    StockExit toEntity(CreateStockExitRequest request);

    StockExitResponse toResponse(StockExit stockExit);
}