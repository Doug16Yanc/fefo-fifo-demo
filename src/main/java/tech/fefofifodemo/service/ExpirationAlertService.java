package tech.fefofifodemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.fefofifodemo.controller.dto.response.ExpirationAlertResponse;
import tech.fefofifodemo.domain.Batch;
import tech.fefofifodemo.domain.ExpirationAlert;
import tech.fefofifodemo.domain.enums.ExpirationAlertStatus;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.mapper.ExpirationAlertMapper;
import tech.fefofifodemo.repository.BatchRepository;
import tech.fefofifodemo.repository.ExpirationAlertRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class ExpirationAlertService {

    private final ExpirationAlertRepository expirationAlertRepository;
    private final BatchRepository batchRepository;
    private final ExpirationAlertMapper expirationAlertMapper;

    public ExpirationAlertService(ExpirationAlertRepository expirationAlertRepository, BatchRepository batchRepository, ExpirationAlertMapper expirationAlertMapper) {
        this.expirationAlertRepository = expirationAlertRepository;
        this.batchRepository = batchRepository;
        this.expirationAlertMapper = expirationAlertMapper;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void runDailyExpirationCheck() {
        log.info("Starting daily validity check...");

        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(90);

        List<Batch> nearExpirationBatches = batchRepository
                .findBatchesNearExpiration(thresholdDate);

        for (Batch batch : nearExpirationBatches) {

            boolean alreadyHasActiveAlert = expirationAlertRepository
                    .existsByBatchAndStatus(batch,ExpirationAlertStatus.ACTIVE);

            long daysRemaining = ChronoUnit.DAYS.between(today, batch.getExpirationDate());

            ExpirationAlert alert = new ExpirationAlert();
            alert.setBatch(batch);
            alert.setAlertDate(today);
            alert.setDaysUntilExpiration((int) daysRemaining);
            alert.setExpirationAlertStatus(ExpirationAlertStatus.ACTIVE);

            expirationAlertRepository.save(alert);
            log.warn("Alert generated for batch {}: expires in {} days",
                    batch.getBatchNumber(), daysRemaining);
        }
    }

    @Transactional(readOnly = true)
    public Page<ExpirationAlertResponse> findAllActiveAlerts(Pageable pageable) {
        return expirationAlertRepository
                .findByExpirationAlertStatus(ExpirationAlertStatus.ACTIVE, pageable)
                .map(expirationAlertMapper::toResponse);
    }

    @Transactional
    public void acknowledgeAlert(Long alertId) {
        ExpirationAlert alert = expirationAlertRepository.findById(alertId)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found"));

        alert.setExpirationAlertStatus(ExpirationAlertStatus.ACKNOWLEDGED);
        expirationAlertRepository.save(alert);
        log.info("Alert {} acknowledged", alertId);
    }

    @Transactional
    public void checkAndGenerateAlerts() {
        runDailyExpirationCheck();
    }
}
