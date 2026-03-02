package tech.fefofifodemo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.fefofifodemo.controller.dto.request.CreateMedicamentRequest;
import tech.fefofifodemo.controller.dto.request.UpdateMedicamentRequest;
import tech.fefofifodemo.controller.dto.response.MedicamentResponse;
import tech.fefofifodemo.domain.Medicament;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;
import tech.fefofifodemo.exception.local.EntityAlreadyExistsException;
import tech.fefofifodemo.exception.local.EntityNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class MedicamentServiceTest extends AbstractIntegrationTest {

    @Autowired
    private MedicamentService medicamentService;

    private CreateMedicamentRequest buildRequest(String name) {
        return new CreateMedicamentRequest(
                name, "Description of the " + name,
                MedicamentCategory.VACCINE, UnitOfMeasure.ML, true
        );
    }

    @Test
    @DisplayName("Should create a medicament successfully")
    void shouldCreateMedicament() {
        MedicamentResponse response = medicamentService.createMedicament(buildRequest("insulin"));

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("Insulin");
        assertThat(response.description()).isEqualTo("Insulin description");
        assertThat(response.medicamentCategory()).isEqualTo(MedicamentCategory.INSULIN);
        assertThat(response.unitOfMeasure()).isEqualTo(UnitOfMeasure.ML);
    }

    @Test
    @DisplayName("Should throw when creating duplicate medicament")
    void shouldThrowOnDuplicateMedicament() {
        medicamentService.createMedicament(buildRequest("Amoxicillin"));

        assertThatThrownBy(() -> medicamentService.createMedicament(buildRequest("Amoxicillin")))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should find medicament by name ignoring case")
    void shouldFindByNameIgnoringCase() {
        medicamentService.createMedicament(buildRequest("Dipirona"));

        Optional<MedicamentResponse> result = medicamentService.findByNameResponse("dipirona");

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Dipirona");
    }

    @Test
    @DisplayName("Should return empty when medicament not found by name")
    void shouldReturnEmptyWhenNotFoundByName() {
        Optional<MedicamentResponse> result = medicamentService.findByNameResponse("Does not exists");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should throw when medicament not found by id")
    void shouldThrowWhenNotFoundById() {
        assertThatThrownBy(() -> medicamentService.findMedicamentById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Should get or create medicament idempotently")
    void shouldGetOrCreateIdempotently() {
        CreateMedicamentRequest request = buildRequest("Paracetamol");

        Medicament first = medicamentService.getOrCreateMedicament(request);
        Medicament second = medicamentService.getOrCreateMedicament(request);

        assertThat(first.getId()).isEqualTo(second.getId());
    }

    @Test
    @DisplayName("Should update medicament successfully")
    void shouldUpdateMedicament() {
        MedicamentResponse created = medicamentService.createMedicament(buildRequest("Ibuprofen"));

        var updateRequest = new UpdateMedicamentRequest(
                "Ibuprofen 400mg", "Anti-inflamatório",
                MedicamentCategory.ANTI_INFLAMMATORY, UnitOfMeasure.ML
        );
        MedicamentResponse updated = medicamentService.updateMedicament(created.id(), updateRequest);

        assertThat(updated.name()).isEqualTo("Ibuprofen 400mg");
        assertThat(updated.description()).isEqualTo("Anti-inflammatory");
        assertThat(updated.medicamentCategory()).isEqualTo(MedicamentCategory.ANTI_INFLAMMATORY);
        assertThat(updated.unitOfMeasure()).isEqualTo(UnitOfMeasure.ML);
    }

    @Test
    @DisplayName("Should throw when updating non-existent medicament")
    void shouldThrowWhenUpdatingNonExistent() {
        var updateRequest = new UpdateMedicamentRequest(
                "X", "unknown", MedicamentCategory.OTHER, UnitOfMeasure.TABLET
        );
        assertThatThrownBy(() -> medicamentService.updateMedicament(999L, updateRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }
}