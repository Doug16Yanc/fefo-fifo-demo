package tech.fefofifodemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.fefofifodemo.domain.views.BatchFefo;

import java.util.List;

@Repository
public interface BatchFefoRepository extends JpaRepository<BatchFefo, Long> {
    List<BatchFefo> findByMedicamentId(Long id);
}
