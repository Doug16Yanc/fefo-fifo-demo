package tech.fefofifodemo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import tech.fefofifodemo.controller.dto.response.ExpirationAlertResponse;
import tech.fefofifodemo.domain.enums.ExpirationAlertStatus;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.service.ExpirationAlertService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpirationAlertController.class)
class ExpirationAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpirationAlertService expirationAlertService;

    private ExpirationAlertResponse buildAlertResponse() {
        return new ExpirationAlertResponse(
                1L,
                LocalDate.now(),
                30,
                ExpirationAlertStatus.ACTIVE,
                123456L
        );
    }

    @Test
    @DisplayName("POST /expiration-alerts/alerts/check → 202 accepted")
    void shouldTriggerExpirationCheck() throws Exception {
        doNothing().when(expirationAlertService).checkAndGenerateAlerts();

        mockMvc.perform(post("/expiration-alerts/alerts/check"))
                .andExpect(status().isAccepted());

        verify(expirationAlertService, times(1)).checkAndGenerateAlerts();
    }

    @Test
    @DisplayName("GET /expiration-alerts/alerts/active → 200 with active alerts")
    void shouldReturnActiveAlerts() throws Exception {
        var page = new PageImpl<>(List.of(buildAlertResponse()), PageRequest.of(0, 10), 1);
        when(expirationAlertService.findAllActiveAlerts(any())).thenReturn(page);

        mockMvc.perform(get("/expiration-alerts/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].daysUntilExpiration").value(30))
                .andExpect(jsonPath("$.content[0].expirationAlertStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].batchNumber").value(123456));
    }

    @Test
    @DisplayName("GET /expiration-alerts/alerts/active → 200 with empty page")
    void shouldReturnEmptyPageWhenNoAlerts() throws Exception {
        var page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(expirationAlertService.findAllActiveAlerts(any())).thenReturn(page);

        mockMvc.perform(get("/expiration-alerts/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("PATCH /expiration-alerts/alerts/{id}/acknowledge → 204 no content")
    void shouldAcknowledgeAlert() throws Exception {
        doNothing().when(expirationAlertService).acknowledgeAlert(eq(1L));

        mockMvc.perform(patch("/expiration-alerts/alerts/1/acknowledge"))
                .andExpect(status().isNoContent());

        verify(expirationAlertService, times(1)).acknowledgeAlert(1L);
    }

    @Test
    @DisplayName("PATCH /expiration-alerts/alerts/{id}/acknowledge → 404 when not found")
    void shouldReturn404WhenAlertNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Alert not found"))
                .when(expirationAlertService).acknowledgeAlert(eq(99L));

        mockMvc.perform(patch("/expiration-alerts/alerts/99/acknowledge"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /expiration-alerts/alerts/active → alertDate field present")
    void shouldReturnAlertDateInResponse() throws Exception {
        var page = new PageImpl<>(List.of(buildAlertResponse()), PageRequest.of(0, 10), 1);
        when(expirationAlertService.findAllActiveAlerts(any())).thenReturn(page);

        mockMvc.perform(get("/expiration-alerts/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].alertDate").value(LocalDate.now().toString()));
    }
}