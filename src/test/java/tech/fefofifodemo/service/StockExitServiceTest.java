package tech.fefofifodemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import tech.fefofifodemo.controller.dto.request.CreateBatchRequest;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.CreateStockExitRequest;
import tech.fefofifodemo.controller.dto.response.StockExitResponse;
import tech.fefofifodemo.domain.Medicament;
import tech.fefofifodemo.domain.enums.BatchStatus;
import tech.fefofifodemo.domain.enums.ExitReason;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;
import tech.fefofifodemo.exception.local.InsufficientStockException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class StockExitServiceTest extends AbstractIntegrationTest {

    @Autowired
    private StockExitService stockExitService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private MedicamentService medicamentService;

    private CreateMedicamentRequest buildMedRequest(String name, MedicamentCategory category, boolean coldChain) {
        return new CreateMedicamentRequest(
                name, "Description of " + name,
                category, UnitOfMeasure.ML, coldChain
        );
    }

    private CreateBatchRequest buildBatchRequest(Long batchNumber, int qty) {
        return new CreateBatchRequest(
                null, batchNumber,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusMonths(6),
                qty, BatchStatus.ACTIVE
        );
    }

    private Medicament setupMedicamentWithStock(String name, MedicamentCategory category,
                                                boolean coldChain, Long batchNumber, int qty) {
        var medRequest = buildMedRequest(name, category, coldChain);
        batchService.registerBatchEntry(medRequest, buildBatchRequest(batchNumber, qty), "Supplier");
        return medicamentService.getOrCreateMedicament(medRequest);
    }

    @Test
    @DisplayName("Should execute FEFO exit for cold chain medicament")
    void shouldExecuteFefoExit() {
        Medicament med = setupMedicamentWithStock(
                "Insulin NPH", MedicamentCategory.INSULIN, true, 100001L, 50
        );
        var request = new CreateStockExitRequest(LocalDate.now(), 10, ExitReason.DISPENSING);

        assertThatCode(() -> stockExitService.executeStockExit(med, request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should execute FIFO exit for common medicament")
    void shouldExecuteFifoExit() {
        Medicament med = setupMedicamentWithStock(
                "Paracetamol 750mg", MedicamentCategory.ANALGESIC, false, 100002L, 100
        );
        var request = new CreateStockExitRequest(LocalDate.now(), 20, ExitReason.DISPENSING);

        assertThatCode(() -> stockExitService.executeStockExit(med, request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should consume multiple batches when quantity spans across them - FEFO")
    void shouldConsumeMultipleBatchesFefo() {
        var medRequest = buildMedRequest("Eritropoetina", MedicamentCategory.BIOTECHNOLOGICAL, true);

        batchService.registerBatchEntry(medRequest,
                new CreateBatchRequest(null, 100003L,
                        LocalDate.now().minusDays(10),
                        LocalDate.now().plusMonths(2),
                        30, BatchStatus.ACTIVE),
                "Supplier");

        batchService.registerBatchEntry(medRequest,
                new CreateBatchRequest(null, 100004L,
                        LocalDate.now().minusDays(5),
                        LocalDate.now().plusMonths(5),
                        30, BatchStatus.ACTIVE),
                "Supplier");

        Medicament med = medicamentService.getOrCreateMedicament(medRequest);
        var request = new CreateStockExitRequest(LocalDate.now(), 50, ExitReason.DISPENSING);

        assertThatCode(() -> stockExitService.executeStockExit(med, request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when quantity exceeds stock")
    void shouldThrowOnInsufficientStock() {
        Medicament med = setupMedicamentWithStock(
                "Amoxillin 500mg", MedicamentCategory.ANTIBIOTIC, false, 100005L, 5
        );
        var request = new CreateStockExitRequest(LocalDate.now(), 999, ExitReason.DISPENSING);

        assertThatThrownBy(() -> stockExitService.executeStockExit(med, request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("994");
    }

    @Test
    @DisplayName("Should list all stock exits paginated")
    void shouldListAllStockExits() {
        Page<StockExitResponse> result = stockExitService.findAllStockExits(PageRequest.of(0, 10));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should list stock exits by batch id")
    void shouldListStockExitsByBatchId() {
        Page<StockExitResponse> result = stockExitService.findStockExitsByBatchId(PageRequest.of(0, 10), 1L);
        assertThat(result).isNotNull();
    }
}