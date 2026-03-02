package tech.fefofifodemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.fefofifodemo.controller.dto.request.CreateStockExitRequest;
import tech.fefofifodemo.controller.dto.response.StockExitResponse;
import tech.fefofifodemo.domain.Medicament;
import tech.fefofifodemo.domain.enums.ExitReason;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.exception.local.InsufficientStockException;
import tech.fefofifodemo.service.MedicamentService;
import tech.fefofifodemo.service.StockExitService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockExitController.class)
class StockExitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StockExitService stockExitService;

    @MockitoBean
    private MedicamentService medicamentService;

    private CreateStockExitRequest buildExitRequest() {
        return new CreateStockExitRequest(LocalDate.now(), 10, ExitReason.DISPENSING);
    }

    private StockExitResponse buildExitResponse() {
        return new StockExitResponse(1L, 10L, LocalDate.now(), ExitReason.DISPENSING);
    }

    private Medicament buildMedicament(MedicamentCategory category, boolean coldChain) {
        Medicament med = new Medicament();
        med.setId(1L);
        med.setName("Insulina");
        med.setMedicamentCategory(category);
        med.setColdChain(coldChain);
        return med;
    }

    @Test
    @DisplayName("POST /stock-exits/execute-exit/{medicamentId} → 200 exit processed")
    void shouldExecuteStockExit() throws Exception {
        when(medicamentService.findMedicamentById(1L))
                .thenReturn(buildMedicament(MedicamentCategory.ANALGESIC, true));
        doNothing().when(stockExitService).executeStockExit(any(), any());

        mockMvc.perform(post("/stock-exits/execute-exit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildExitRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock exit processed successfully!"));
    }

    @Test
    @DisplayName("POST /stock-exits/execute-exit/{medicamentId} → 404 when medicament not found")
    void shouldReturn404WhenMedicamentNotFound() throws Exception {
        when(medicamentService.findMedicamentById(99L))
                .thenThrow(new EntityNotFoundException("Medication not found"));

        mockMvc.perform(post("/stock-exits/execute-exit/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildExitRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /stock-exits/execute-exit/{medicamentId} → 422 when insufficient stock")
    void shouldReturn422OnInsufficientStock() throws Exception {
        var request = new CreateStockExitRequest(LocalDate.now(), 999, ExitReason.DISPENSING);

        when(medicamentService.findMedicamentById(1L))
                .thenReturn(buildMedicament(MedicamentCategory.OTHER, false));
        doThrow(new InsufficientStockException("Insufficient stock. Shortage of 950."))
                .when(stockExitService).executeStockExit(any(), any());

        mockMvc.perform(post("/stock-exits/execute-exit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /stock-exits/find-all → 200 paginated")
    void shouldReturnAllStockExits() throws Exception {
        var page = new PageImpl<>(List.of(buildExitResponse()), PageRequest.of(0, 10), 1);
        when(stockExitService.findAllStockExits(any())).thenReturn(page);

        mockMvc.perform(get("/stock-exits/find-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].batchId").value(10))
                .andExpect(jsonPath("$.content[0].exitReason").value("DISPENSING"));
    }

    @Test
    @DisplayName("GET /stock-exits/find-by-batch/{batchId} → 200 filtered by batch")
    void shouldReturnExitsByBatch() throws Exception {
        var page = new PageImpl<>(List.of(buildExitResponse()), PageRequest.of(0, 10), 1);
        when(stockExitService.findStockExitsByBatchId(any(), eq(10L))).thenReturn(page);

        mockMvc.perform(get("/stock-exits/find-by-batch/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].batchId").value(10))
                .andExpect(jsonPath("$.content[0].exitReason").value("DISPENSING"));
    }
}