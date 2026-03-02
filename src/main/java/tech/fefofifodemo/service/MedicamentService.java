package tech.fefofifodemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.UpdateMedicamentRequest;
import tech.fefofifodemo.controller.dto.response.MedicamentResponse;
import tech.fefofifodemo.domain.Medicament;
import tech.fefofifodemo.exception.local.EntityAlreadyExistsException;
import tech.fefofifodemo.exception.local.EntityNotFoundException;
import tech.fefofifodemo.mapper.MedicamentMapper;
import tech.fefofifodemo.repository.MedicamentRepository;

import java.util.Optional;

@Service
@Slf4j
public class MedicamentService {

    private final MedicamentRepository medicamentRepository;
    private final MedicamentMapper medicamentMapper;

    public MedicamentService(MedicamentRepository medicamentRepository, MedicamentMapper medicamentMapper) {
        this.medicamentRepository = medicamentRepository;
        this.medicamentMapper = medicamentMapper;
    }

    public Optional<Medicament> findByName(String name) {
        return medicamentRepository.findByNameIgnoreCase(name);
    }

    @Transactional
    public MedicamentResponse createMedicament(CreateMedicamentRequest request) {
        log.info("Creating new medicament: {}", request.name());

        if (medicamentRepository.existsByNameIgnoreCase(request.name())) {
            throw new EntityAlreadyExistsException("Medicament already registered with this name");
        }

        var medicament = medicamentMapper.toEntity(request);
        var saved = medicamentRepository.save(medicament);
        return medicamentMapper.toResponse(saved);
    }

    @Transactional
    public Medicament getOrCreateMedicament(CreateMedicamentRequest request) {
        return medicamentRepository.findByNameIgnoreCase(request.name())
                .orElseGet(() -> {
                    var newMed = medicamentMapper.toEntity(request);
                    return medicamentRepository.save(newMed);
                });
    }

    public Medicament findMedicamentById(Long medicamentId) {
        return medicamentRepository.findMedicamentById(medicamentId)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found"));
    }

    public Optional<MedicamentResponse> findByNameResponse(String name) {
        return medicamentRepository.findByNameIgnoreCase(name)
                .map(medicamentMapper::toResponse);
    }

    @Transactional
    public MedicamentResponse updateMedicament(Long id, UpdateMedicamentRequest request) {
        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicament not found with id: " + id));

        medicamentMapper.updateEntityFromDto(request, medicament);

        return medicamentMapper.toResponse(medicamentRepository.save(medicament));
    }
}
