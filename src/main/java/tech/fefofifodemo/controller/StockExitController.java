package tech.fefofifodemo.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.fefofifodemo.controller.dto.request.CreateStockExitRequest;
import tech.fefofifodemo.controller.dto.response.StockExitResponse;
import tech.fefofifodemo.service.MedicamentService;
import tech.fefofifodemo.service.StockExitService;

@RestController
@RequestMapping("/stock-exits")
@Slf4j
public class StockExitController {

    private final StockExitService stockExitService;
    private final MedicamentService medicamentService;

    public StockExitController(StockExitService stockExitService, MedicamentService medicamentService) {
        this.stockExitService = stockExitService;
        this.medicamentService = medicamentService;
    }

    @PostMapping("/execute-exit/{medicamentId}")
    public ResponseEntity<String> executeExit(@PathVariable Long medicamentId,
                                              @Valid @RequestBody CreateStockExitRequest request) {

        log.info("REST request to execute stock exit for medicament id: {}, quantity: {}",
                medicamentId, request.quantity());

        var medicament = medicamentService.findMedicamentById(medicamentId);

        stockExitService.executeStockExit(medicament, request);

        return ResponseEntity.ok("Stock exit processed successfully!");
    }

    @GetMapping("/find-all")
    public ResponseEntity<Page<StockExitResponse>> findAll(
            @PageableDefault(size = 10, sort = "alertDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(stockExitService.findAllStockExits(pageable));
    }

    @GetMapping("/find-by-batch/{batchId}")
    public ResponseEntity<Page<StockExitResponse>> findStockExitsByBatchId(
            @PageableDefault(size = 10, sort = "alertDate", direction = Sort.Direction.DESC) Pageable pageable,
            Long batchId) {
        return ResponseEntity.ok().body(stockExitService.findStockExitsByBatchId(pageable, batchId));
    }
}
