package tech.fefofifodemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.fefofifodemo.controller.dto.response.ExpirationAlertResponse;
import tech.fefofifodemo.service.ExpirationAlertService;

@RestController
@RequestMapping("/expiration-alerts")
@Slf4j
public class ExpirationAlertController {

    private final ExpirationAlertService expirationAlertService;

    public ExpirationAlertController(ExpirationAlertService expirationAlertService) {
        this.expirationAlertService = expirationAlertService;
    }

    @PostMapping("/alerts/check")
    public ResponseEntity<Void> triggerExpirationCheck() {
        log.info("Manually triggering expiration alerts check");
        expirationAlertService.checkAndGenerateAlerts();
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/alerts/active")
    public ResponseEntity<Page<ExpirationAlertResponse>> getActiveAlerts(
            @PageableDefault(size = 10, sort = "alertDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(expirationAlertService.findAllActiveAlerts(pageable));
    }

    @PatchMapping("/alerts/{id}/acknowledge")
    public ResponseEntity<Void> acknowledgeAlert(@PathVariable Long id) {
        log.info("Request to acknowledge alert with id: {}", id);
        expirationAlertService.acknowledgeAlert(id);
        return ResponseEntity.noContent().build();
    }
}
