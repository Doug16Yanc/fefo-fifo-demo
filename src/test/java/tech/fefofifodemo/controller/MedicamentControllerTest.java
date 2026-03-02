package tech.fefofifodemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.UpdateMedicamentRequest;
import tech.fefofifodemo.controller.dto.response.MedicamentResponse;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;
import tech.fefofifodemo.exception.local.EntityAlreadyExistsException;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.service.MedicamentService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicamentController.class)
class MedicamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MedicamentService medicamentService;

    private CreateMedicamentRequest buildCreateRequest() {
        return new CreateMedicamentRequest(
                "Insulin", "Insulin fast action",
                MedicamentCategory.INSULIN, UnitOfMeasure.ML, true
        );
    }

    private MedicamentResponse buildResponse() {
        return new MedicamentResponse(
                1L, "Insulin", "Insulin fast action",
                MedicamentCategory.INSULIN, UnitOfMeasure.ML
        );
    }

    @Test
    @DisplayName("POST /medicaments → 200 with created medicament")
    void shouldCreateMedicament() throws Exception {
        when(medicamentService.createMedicament(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/medicaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Insulina"))
                .andExpect(jsonPath("$.description").value("Insulina ação rápida"))
                .andExpect(jsonPath("$.medicamentCategory").value("COLD_CHAIN"))
                .andExpect(jsonPath("$.unitOfMeasure").value("ML"));
    }

    @Test
    @DisplayName("POST /medicaments → 409 when medicament already exists")
    void shouldReturn409OnDuplicate() throws Exception {
        when(medicamentService.createMedicament(any()))
                .thenThrow(new EntityAlreadyExistsException("Medicament already registered"));

        mockMvc.perform(post("/medicaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /medicaments/update-medicament/{id} → 200 with updated medicament")
    void shouldUpdateMedicament() throws Exception {
        var updateRequest = new UpdateMedicamentRequest(
                "Insulina 10UI", "Atualizada",
                MedicamentCategory.BLOOD_DERIVATIVE, UnitOfMeasure.ML
        );
        when(medicamentService.updateMedicament(eq(1L), any())).thenReturn(buildResponse());

        mockMvc.perform(put("/medicaments/update-medicament/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Insulina"));
    }

    @Test
    @DisplayName("PUT /medicaments/update-medicament/{id} → 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        var updateRequest = new UpdateMedicamentRequest(
                "X", "desc", MedicamentCategory.OTHER, UnitOfMeasure.UNIT
        );
        when(medicamentService.updateMedicament(eq(99L), any()))
                .thenThrow(new EntityNotFoundException("Medicament not found with id: 99"));

        mockMvc.perform(put("/medicaments/update-medicament/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /medicaments/search?name=Insulina → 200")
    void shouldFindByName() throws Exception {
        when(medicamentService.findByNameResponse("Insulina"))
                .thenReturn(Optional.of(buildResponse()));

        mockMvc.perform(get("/medicaments/search").param("name", "Insulina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Insulina"))
                .andExpect(jsonPath("$.description").value("Insulina ação rápida"));
    }

    @Test
    @DisplayName("GET /medicaments/search?name=Unknown → 404")
    void shouldReturn404WhenNotFoundByName() throws Exception {
        when(medicamentService.findByNameResponse("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/medicaments/search").param("name", "Unknown"))
                .andExpect(status().isNotFound());
    }
}
