package tech.fefofifodemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.fefofifodemo.domain.Medicament;

import java.util.Optional;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    Optional<Medicament> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Optional<Medicament> findMedicamentById(Long medicamentId);
}
