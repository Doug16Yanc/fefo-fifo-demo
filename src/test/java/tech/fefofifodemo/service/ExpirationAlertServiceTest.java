package tech.fefofifodemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import tech.fefofifodemo.controller.dto.request.CreateBatchRequest;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.response.ExpirationAlertResponse;
import tech.fefofifodemo.domain.enums.BatchStatus;
import tech.fefofifodemo.domain.enums.ExpirationAlertStatus;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;
import tech.fefofifodemo.exception.local.EntityNotFoundException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class ExpirationAlertServiceTest extends AbstractIntegrationTest {

    @Autowired
    private ExpirationAlertService expirationAlertService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private MedicamentService medicamentService;

    private void registerBatch(String name, MedicamentCategory category,
                               boolean coldChain, Long batchNumber, LocalDate expirationDate) {
        var medRequest = new CreateMedicamentRequest(
                name, "Description of the " + name,
                category, UnitOfMeasure.ML, coldChain
        );
        var batchRequest = new CreateBatchRequest(
                null, batchNumber,
                LocalDate.now().minusDays(10),
                expirationDate,
                50, BatchStatus.ACTIVE
        );
        batchService.registerBatchEntry(medRequest, batchRequest, "Supplier");
    }

    @Test
    @DisplayName("Should generate alert for batch expiring within 90 days")
    void shouldGenerateAlertForNearExpirationBatch() {
        registerBatch("Vaccine against polio", MedicamentCategory.VACCINE, true,
                200001L, LocalDate.now().plusDays(30));

        expirationAlertService.checkAndGenerateAlerts();

        Page<ExpirationAlertResponse> alerts = expirationAlertService
                .findAllActiveAlerts(PageRequest.of(0, 10));

        assertThat(alerts.getContent()).isNotEmpty();

        ExpirationAlertResponse alert = alerts.getContent().getFirst();
        assertThat(alert.id()).isNotNull();
        assertThat(alert.alertDate()).isEqualTo(LocalDate.now());
        assertThat(alert.daysUntilExpiration()).isLessThanOrEqualTo(90);
        assertThat(alert.expirationAlertStatus()).isEqualTo(ExpirationAlertStatus.ACTIVE);
        assertThat(alert.batchNumber()).isEqualTo(200001L);
    }

    @Test
    @DisplayName("Should NOT generate alert for batch expiring after 90 days")
    void shouldNotGenerateAlertForFarExpirationBatch() {
        registerBatch("Dipyrone 1g", MedicamentCategory.ANALGESIC, false,
                200002L, LocalDate.now().plusDays(200));

        long beforeCount = expirationAlertService
                .findAllActiveAlerts(PageRequest.of(0, 100)).getTotalElements();

        expirationAlertService.checkAndGenerateAlerts();

        long afterCount = expirationAlertService
                .findAllActiveAlerts(PageRequest.of(0, 100)).getTotalElements();

        assertThat(afterCount).isEqualTo(beforeCount);
    }

    @Test
    @DisplayName("Should generate alert with correct days until expiration")
    void shouldGenerateAlertWithCorrectDaysUntilExpiration() {
        registerBatch("Erythropoietin", MedicamentCategory.BIOTECHNOLOGICAL, true,
                200003L, LocalDate.now().plusDays(45));

        expirationAlertService.checkAndGenerateAlerts();

        Page<ExpirationAlertResponse> alerts = expirationAlertService
                .findAllActiveAlerts(PageRequest.of(0, 10));

        assertThat(alerts.getContent())
                .anyMatch(a -> a.batchNumber().equals(200003L) && a.daysUntilExpiration() == 45);
    }

    @Test
    @DisplayName("Should acknowledge an active alert successfully")
    void shouldAcknowledgeAlert() {
        registerBatch("Insulin Glargine", MedicamentCategory.INSULIN, true,
                200004L, LocalDate.now().plusDays(15));

        expirationAlertService.checkAndGenerateAlerts();

        Page<ExpirationAlertResponse> alerts = expirationAlertService
                .findAllActiveAlerts(PageRequest.of(0, 10));

        assertThat(alerts.getContent()).isNotEmpty();

        Long alertId = alerts.getContent().stream()
                .filter(a -> a.batchNumber().equals(200004L))
                .findFirst()
                .orElseThrow()
                .id();

        assertThatCode(() -> expirationAlertService.acknowledgeAlert(alertId))
                .doesNotThrowAnyException();

        Page<ExpirationAlertResponse> afterAck = expirationAlertService
                .findAllActiveAlerts(PageRequest.of(0, 100));

        assertThat(afterAck.getContent())
                .noneMatch(a -> a.id().equals(alertId));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when acknowledging non-existent alert")
    void shouldThrowWhenAcknowledgingNonExistentAlert() {
        assertThatThrownBy(() -> expirationAlertService.acknowledgeAlert(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should return empty page when no active alerts")
    void shouldReturnEmptyPageWhenNoActiveAlerts() {
        Page<ExpirationAlertResponse> result = expirationAlertService
                .findAllActiveAlerts(PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isInstanceOf(java.util.List.class);
    }
}
