package tech.fefofifodemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.fefofifodemo.controller.dto.request.CreateStockExitRequest;
import tech.fefofifodemo.controller.dto.response.StockExitResponse;
import tech.fefofifodemo.domain.Batch;
import tech.fefofifodemo.domain.Medicament;
import tech.fefofifodemo.domain.StockExit;
import tech.fefofifodemo.domain.views.BatchFefo;
import tech.fefofifodemo.domain.views.BatchFifo;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.exception.local.InsufficientStockException;
import tech.fefofifodemo.mapper.StockExitMapper;
import tech.fefofifodemo.repository.BatchFefoRepository;
import tech.fefofifodemo.repository.BatchFifoRepository;
import tech.fefofifodemo.repository.BatchRepository;
import tech.fefofifodemo.repository.StockExitRepository;

import java.util.List;

@Service
@Slf4j
public class StockExitService {

    private final StockExitRepository stockExitRepository;
    private final BatchRepository batchRepository;
    private final BatchFefoRepository batchFefoRepository;
    private final BatchFifoRepository batchFifoRepository;
    private final StockExitMapper stockExitMapper;

    public StockExitService(StockExitRepository stockExitRepository,
                            BatchRepository batchRepository,
                            BatchFefoRepository batchFefoRepository,
                            BatchFifoRepository batchFifoRepository,
                            StockExitMapper stockExitMapper) {
        this.stockExitRepository = stockExitRepository;
        this.batchRepository = batchRepository;
        this.batchFefoRepository = batchFefoRepository;
        this.batchFifoRepository = batchFifoRepository;
        this.stockExitMapper = stockExitMapper;
    }

    @Transactional
    public void executeStockExit(Medicament medicament, CreateStockExitRequest request) {

        boolean useFefo = medicament.getMedicamentCategory()
                .requiresFEFO(medicament.getColdChain());

        int remaining = request.quantity();

        if (useFefo) {
            log.info("Processing FEFO strategy for : {}", medicament.getName());

            List<BatchFefo> fefoBatches = batchFefoRepository.findByMedicamentId(medicament.getId());

            for (BatchFefo view : fefoBatches) {
                if (remaining <= 0) break;
                remaining = processBatchExit(view.getBatchId(), remaining, request);
            }
        } else {
            log.info("Processing FIFO strategy for: {}", medicament.getName());

            List<BatchFifo> fifoBatches = batchFifoRepository.findBByMedicamentId(medicament.getId());

            for (BatchFifo view : fifoBatches) {
                if (remaining <= 0) break;
                remaining = processBatchExit(view.getBatchId(), remaining, request);
            }
        }

        if (remaining > 0) {
            throw new InsufficientStockException("Insufficient stock. There is a shortage of " + remaining + " medicaments.");
        }
    }

    public Page<StockExitResponse> findAllStockExits(Pageable pageable) {
        return stockExitRepository.findAll(pageable)
                .map(stockExitMapper::toResponse);
    }

    public Page<StockExitResponse> findStockExitsByBatchId(Pageable pageable, Long batchId) {
        return stockExitRepository.findStockExitsByBatchId(pageable, batchId)
                .map(stockExitMapper::toResponse);
    }

    private int processBatchExit(Long batchId, int remaining, CreateStockExitRequest request) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("Batch not found"));

        int quantityToTake = Math.min(batch.getCurrentQuantity(), remaining);

        batch.setCurrentQuantity(batch.getCurrentQuantity() - quantityToTake);
        batchRepository.save(batch);

        StockExit stockExit = stockExitMapper.toEntity(request);
        stockExit.setBatch(batch);
        stockExit.setQuantity(quantityToTake);
        stockExitRepository.save(stockExit);

        return remaining - quantityToTake;
    }

}
