package tech.fefofifodemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.fefofifodemo.domain.views.BatchFifo;

import java.util.List;

@Repository
public interface BatchFifoRepository extends JpaRepository<BatchFifo, Long> {
    List<BatchFifo> findMedicamentById(Long id);
}
