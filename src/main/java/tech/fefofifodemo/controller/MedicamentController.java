package tech.fefofifodemo.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.UpdateMedicamentRequest;
import tech.fefofifodemo.controller.dto.response.MedicamentResponse;
import tech.fefofifodemo.service.MedicamentService;

@RestController
@RequestMapping("/medicaments")
@Slf4j
public class MedicamentController {

    private final MedicamentService medicamentService;

    public MedicamentController(MedicamentService medicamentService) {
        this.medicamentService = medicamentService;
    }

    @PostMapping
    public ResponseEntity<MedicamentResponse> create(@Valid @RequestBody CreateMedicamentRequest request) {
        log.info("REST request to create medicament: {}", request.name());
        var response = medicamentService.createMedicament(request);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update-medicament/{id}")
    public ResponseEntity<MedicamentResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateMedicamentRequest request) {
        log.info("REST request to update medicament id: {}", id);
        var response = medicamentService.updateMedicament(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<MedicamentResponse> findByName(@RequestParam String name) {
        return medicamentService.findByNameResponse(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
