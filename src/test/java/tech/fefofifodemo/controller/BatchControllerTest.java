package tech.fefofifodemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.fefofifodemo.controller.dto.request.*;
import tech.fefofifodemo.controller.dto.response.BatchResponse;
import tech.fefofifodemo.domain.enums.BatchStatus;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;
import tech.fefofifodemo.exception.local.EntityAlreadyExistsException;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.service.BatchService;
import tech.fefofifodemo.service.ExpirationAlertService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BatchController.class)
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BatchService batchService;

    @MockitoBean
    private ExpirationAlertService expirationAlertService;

    private CreateBatchRequest buildBatchRequest() {
        return new CreateBatchRequest(
                1L, 123456L,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6),
                100, BatchStatus.ACTIVE
        );
    }

    private BatchResponse buildBatchResponse() {
        return new BatchResponse(
                1L, 123456L,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6),
                100, 100, BatchStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("POST /batches/create-batch → 200 with batch response")
    void shouldCreateBatch() throws Exception {
        when(batchService.createBatch(any())).thenReturn(buildBatchResponse());

        mockMvc.perform(post("/batches/create-batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildBatchRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.batchNumber").value(123456))
                .andExpect(jsonPath("$.initialQuantity").value(100))
                .andExpect(jsonPath("$.batchStatus").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /batches/create-batch → 409 when batch already exists")
    void shouldReturn409OnDuplicateBatch() throws Exception {
        when(batchService.createBatch(any()))
                .thenThrow(new EntityAlreadyExistsException("Batch already registered."));

        mockMvc.perform(post("/batches/create-batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildBatchRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /batches/registry-entry/{supplier} → 200")
    void shouldRegisterBatchEntry() throws Exception {
        var medRequest = new CreateMedicamentRequest(
                "Insulin", "Insulin fast action",
                MedicamentCategory.INSULIN, UnitOfMeasure.ML, true
        );
        var inventoryRequest = new InventoryEntryRequest(medRequest, buildBatchRequest());

        doNothing().when(batchService).registerBatchEntry(any(), any(), eq("Supplier X"));

        mockMvc.perform(post("/batches/registry-entry/FornecedorX")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Batch entry registered successfully!"));
    }

    @Test
    @DisplayName("GET /batches/fefo → 200 with FEFO ordered page")
    void shouldReturnFefoBatches() throws Exception {
        var page = new PageImpl<>(List.of(buildBatchResponse()), PageRequest.of(0, 10), 1);
        when(batchService.getBatchesByFefo(any())).thenReturn(page);

        mockMvc.perform(get("/batches/fefo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].batchNumber").value(123456))
                .andExpect(jsonPath("$.content[0].batchStatus").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /batches/fifo → 200 with FIFO ordered page")
    void shouldReturnFifoBatches() throws Exception {
        var page = new PageImpl<>(List.of(buildBatchResponse()), PageRequest.of(0, 10), 1);
        when(batchService.getBatchesByFifo(any())).thenReturn(page);

        mockMvc.perform(get("/batches/fifo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].batchNumber").value(123456));
    }

    @Test
    @DisplayName("PUT /batches/update-batch/{id} → 200 with updated batch")
    void shouldUpdateBatch() throws Exception {
        var updateRequest = new UpdateBatchRequest(
                123456L,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(8),
                100
        );
        when(batchService.updateBatch(eq(1L), any())).thenReturn(buildBatchResponse());

        mockMvc.perform(put("/batches/update-batch/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /batches/update-batch/{id} → 404 when not found")
    void shouldReturn404OnBatchNotFound() throws Exception {
        var updateRequest = new UpdateBatchRequest(
                123456L,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6),
                100
        );
        when(batchService.updateBatch(eq(99L), any()))
                .thenThrow(new EntityNotFoundException("Batch with id: 99 not found."));

        mockMvc.perform(put("/batches/update-batch/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }
}