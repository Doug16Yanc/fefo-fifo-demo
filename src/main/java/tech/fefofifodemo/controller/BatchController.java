package tech.fefofifodemo.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.fefofifodemo.controller.dto.request.CreateBatchRequest;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.InventoryEntryRequest;
import tech.fefofifodemo.controller.dto.request.UpdateBatchRequest;
import tech.fefofifodemo.controller.dto.response.BatchFefoResponse;
import tech.fefofifodemo.controller.dto.response.BatchFifoResponse;
import tech.fefofifodemo.controller.dto.response.BatchResponse;
import tech.fefofifodemo.controller.dto.response.ExpirationAlertResponse;
import tech.fefofifodemo.service.BatchService;
import tech.fefofifodemo.service.ExpirationAlertService;

import java.util.List;

@RestController
@RequestMapping("/batches")
@Slf4j
public class BatchController {

    private final BatchService batchService;
    private final ExpirationAlertService expirationAlertService;

    public BatchController(BatchService batchService, ExpirationAlertService expirationAlertService) {
        this.batchService = batchService;
        this.expirationAlertService = expirationAlertService;
    }

    @PostMapping("/create-batch")
    public ResponseEntity<BatchResponse> createBatch(@Valid @RequestBody CreateBatchRequest request) {
        var response = batchService.createBatch(request);
        log.info("Creating batch with batch number: {} ", request.batchNumber());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/registry-entry/{supplier}")
    public ResponseEntity<String> registerBatchEntry(
            @Valid @RequestBody InventoryEntryRequest request,
            @PathVariable String supplier) {

        batchService.registerBatchEntry(
                request.medicament(),
                request.batch(),
                supplier
        );

        log.info("Registering entry for medicament: {} and batch: {}",
                request.medicament().name(), request.batch().batchNumber());

        return ResponseEntity.ok().body("Batch entry registered successfully!");
    }

    @GetMapping("/fefo")
    public ResponseEntity<Page<BatchFefoResponse>> getBatchesByFefo(
            @PageableDefault(size = 10, sort = "expirationDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Fetching batches ordered by FEFO strategy");
        return ResponseEntity.ok(batchService.getBatchesByFefo(pageable));
    }

    @GetMapping("/fifo")
    public ResponseEntity<Page<BatchFifoResponse>> getBatchesByFifo(
            @PageableDefault(size = 10, sort = "entryDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Fetching batches ordered by FIFO strategy");
        return ResponseEntity.ok(batchService.getBatchesByFifo(pageable));
    }


    @PutMapping("/update-batch/{id}")
    public ResponseEntity<BatchResponse> updateBatch(@PathVariable Long id, @Valid @RequestBody UpdateBatchRequest request) {
        var response = batchService.updateBatch(id, request);
        log.info("Updating batch with batch id: {}", id);
        return ResponseEntity.ok().body(response);
    }
}
