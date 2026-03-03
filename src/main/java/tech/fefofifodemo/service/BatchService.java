package tech.fefofifodemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.fefofifodemo.controller.dto.request.CreateBatchRequest;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.UpdateBatchRequest;
import tech.fefofifodemo.controller.dto.response.BatchFefoResponse;
import tech.fefofifodemo.controller.dto.response.BatchFifoResponse;
import tech.fefofifodemo.controller.dto.response.BatchResponse;
import tech.fefofifodemo.domain.Batch;
import tech.fefofifodemo.domain.Medicament;
import tech.fefofifodemo.domain.StockEntry;
import tech.fefofifodemo.exception.local.EntityAlreadyExistsException;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.exception.local.IllegalStateException;
import tech.fefofifodemo.mapper.BatchMapper;
import tech.fefofifodemo.repository.BatchFefoRepository;
import tech.fefofifodemo.repository.BatchFifoRepository;
import tech.fefofifodemo.repository.BatchRepository;
import tech.fefofifodemo.repository.StockEntryRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BatchService {

    private final BatchRepository batchRepository;
    private final BatchFefoRepository batchFefoRepository;
    private final BatchFifoRepository batchFifoRepository;
    private final StockEntryRepository stockEntryRepository;
    private final MedicamentService medicamentService;
    private final BatchMapper batchMapper;

    public BatchService(BatchRepository batchRepository,
                        BatchFefoRepository batchFefoRepository,
                        BatchFifoRepository batchFifoRepository,
                        StockEntryRepository stockEntryRepository,
                        MedicamentService medicamentService,
                        BatchMapper batchMapper) {
        this.batchRepository = batchRepository;
        this.batchFefoRepository = batchFefoRepository;
        this.batchFifoRepository = batchFifoRepository;
        this.stockEntryRepository = stockEntryRepository;
        this.medicamentService = medicamentService;
        this.batchMapper = batchMapper;
    }

    @Transactional
    public BatchResponse createBatch(CreateBatchRequest request) {
        if (batchRepository.existsByBatchNumber(request.batchNumber())) {
            throw new EntityAlreadyExistsException("Batch " + request.batchNumber() + " already registered.");
        }

        Medicament medicament = medicamentService.findMedicamentById(request.medicamentId());

        Batch batch = batchMapper.toEntity(request);

        batch.setMedicament(medicament);

        Batch saved = batchRepository.save(batch);

        return batchMapper.toResponse(saved);
    }

    @Transactional
    public void registerBatchEntry(CreateMedicamentRequest medRequest, CreateBatchRequest batchRequest, String supplier) {
        Medicament medicament = medicamentService.getOrCreateMedicament(medRequest);

        Batch batch = batchRepository.findExistingBatch(
                medicament,
                batchRequest.batchNumber(),
                batchRequest.manufacturingDate(),
                batchRequest.expirationDate()
        ).orElseGet(() -> batchMapper.toEntity(batchRequest));

        boolean isBatchNew = batch.getId() == null;

        if (isBatchNew) {
            batch.setMedicament(medicament);
        } else {
            batch.setCurrentQuantity(batch.getCurrentQuantity() + batchRequest.initialQuantity());
        }

        Batch savedBatch = batchRepository.save(batch);

        StockEntry entry = new StockEntry();
        entry.setBatch(savedBatch);
        entry.setQuantity(batchRequest.initialQuantity());
        entry.setEntryDate(LocalDate.now());
        entry.setSupplier(supplier);

        stockEntryRepository.save(entry);


        log.info("Registered entry: Batch {}, quantity {}, medicament {}",
                savedBatch.getBatchNumber(), batchRequest.initialQuantity(), medicament.getName());
    }

    @Transactional
    public BatchResponse updateBatch(Long id, UpdateBatchRequest request) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Batch with id :" + id + " not found."));

        boolean hasExists = !batch.getStockExists().isEmpty();

        if (hasExists) {
            if (!batch.getExpirationDate().equals(request.expirationDate()) ||
                !batch.getManufacturingDate().equals(request.manufacturingDate())) {
                throw new IllegalStateException("It is not possible to change the dates of a batch that already has recorded departures.");
            }
        }

        batchMapper.updateEntityFromDto(request, batch);

        return batchMapper.toResponse(batchRepository.save(batch));
    }

    public Page<BatchFefoResponse> getBatchesByFefo(Pageable pageable) {
        return batchFefoRepository.findAll(pageable)
                .map(batchMapper::toFefoResponse);
    }

    public Page<BatchFifoResponse> getBatchesByFifo(Pageable pageable) {
        return batchFifoRepository.findAll(pageable)
                .map(batchMapper::toFifoResponse);
    }
}
