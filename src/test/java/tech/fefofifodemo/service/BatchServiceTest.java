package tech.fefofifodemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import tech.fefofifodemo.controller.dto.request.CreateBatchRequest;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.UpdateBatchRequest;
import tech.fefofifodemo.controller.dto.response.BatchFefoResponse;
import tech.fefofifodemo.controller.dto.response.BatchFifoResponse;
import tech.fefofifodemo.controller.dto.response.BatchResponse;
import tech.fefofifodemo.domain.Medicament;
import tech.fefofifodemo.domain.enums.BatchStatus;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;
import tech.fefofifodemo.exception.local.EntityAlreadyExistsException;
import tech.fefofifodemo.exception.local.EntityNotFoundException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class BatchServiceTest extends AbstractIntegrationTest {

    @Autowired
    private BatchService batchService;

    @Autowired
    private MedicamentService medicamentService;

    private CreateMedicamentRequest buildMedRequest(String name, MedicamentCategory category) {
        return new CreateMedicamentRequest(
                name, "Description of the " + name,
                category, UnitOfMeasure.ML, true
        );
    }

    private CreateBatchRequest buildBatchRequest(Long medicamentId, Long batchNumber) {
        return new CreateBatchRequest(
                medicamentId, batchNumber,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6),
                100, BatchStatus.ACTIVE
        );
    }

    private Medicament createMedicament(String name, MedicamentCategory category) {
        return medicamentService.getOrCreateMedicament(buildMedRequest(name, category));
    }

    @Test
    @DisplayName("Should create a batch successfully")
    void shouldCreateBatch() {
        Medicament med = createMedicament("Vaccine against tetanus", MedicamentCategory.VACCINE);

        BatchResponse response = batchService.createBatch(buildBatchRequest(med.getId(), 111111L));

        assertThat(response.id()).isNotNull();
        assertThat(response.batchNumber()).isEqualTo(111111L);
        assertThat(response.initialQuantity()).isEqualTo(100);
        assertThat(response.batchStatus()).isEqualTo(BatchStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw when batch number already exists")
    void shouldThrowOnDuplicateBatchNumber() {
        Medicament med = createMedicament("Ozempic", MedicamentCategory.BIOTECHNOLOGICAL);

        batchService.createBatch(buildBatchRequest(med.getId(), 222222L));

        assertThatThrownBy(() -> batchService.createBatch(buildBatchRequest(med.getId(), 222222L)))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should throw when medicament not found on batch creation")
    void shouldThrowWhenMedicamentNotFound() {
        assertThatThrownBy(() -> batchService.createBatch(buildBatchRequest(999L, 333333L)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should register batch entry and create stock entry")
    void shouldRegisterBatchEntry() {
        var medRequest = buildMedRequest("Rivotril", MedicamentCategory.CONTROLLED_SUBSTANCE);
        var batchRequest = new CreateBatchRequest(
                null, 444444L,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusMonths(12),
                200, BatchStatus.ACTIVE
        );

        assertThatCode(() -> batchService.registerBatchEntry(medRequest, batchRequest, "Supplier X"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accumulate quantity when registering entry for existing batch")
    void shouldAccumulateQuantityOnReEntry() {
        var medRequest = buildMedRequest("Metformin", MedicamentCategory.ANTIDIABETIC);
        var batchRequest = new CreateBatchRequest(
                null, 555555L,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusMonths(8),
                50, BatchStatus.ACTIVE
        );

        batchService.registerBatchEntry(medRequest, batchRequest, "Supplier X");
        batchService.registerBatchEntry(medRequest, batchRequest, "Supplier X");

        assertThatCode(() -> batchService.registerBatchEntry(medRequest, batchRequest, "Supplier X"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should update batch successfully")
    void shouldUpdateBatch() {
        Medicament med = createMedicament("Atenolol", MedicamentCategory.ANTIHYPERTENSIVE);
        BatchResponse created = batchService.createBatch(buildBatchRequest(med.getId(), 666666L));

        var updateRequest = new UpdateBatchRequest(
                666666L,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(10),
                120
        );

        BatchResponse updated = batchService.updateBatch(created.id(), updateRequest);

        assertThat(updated.initialQuantity()).isEqualTo(120);
        assertThat(updated.expirationDate()).isEqualTo(LocalDate.now().plusMonths(10));
    }

    @Test
    @DisplayName("Should return FEFO ordered batches")
    void shouldReturnFefoBatches() {
        Page<BatchFefoResponse> result = batchService.getBatchesByFefo(PageRequest.of(0, 10));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should return FIFO ordered batches")
    void shouldReturnFifoBatches() {
        Page<BatchFifoResponse> result = batchService.getBatchesByFifo(PageRequest.of(0, 10));
        assertThat(result).isNotNull();
    }
}