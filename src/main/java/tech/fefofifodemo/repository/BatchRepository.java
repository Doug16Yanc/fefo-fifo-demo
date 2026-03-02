package tech.fefofifodemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.fefofifodemo.domain.Batch;
import tech.fefofifodemo.domain.Medicament;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    boolean existsByBatchNumber(Long batchNumber);

    Optional<Batch> findByBatchNumber(Long batchNumber);

    @Query("""
       SELECT b FROM Batch b
       WHERE b.medicament = :medicament
       AND b.batchNumber = :batchNumber
       AND b.manufacturingDate = :mfgDate
       AND b.expirationDate = :expDate
    """)
    Optional<Batch> findExistingBatch(
            @Param("medicament") Medicament medicament,
            @Param("batchNumber") Long batchNumber,
            @Param("mfgDate") LocalDate manufacturingDate,
            @Param("expDate") LocalDate expirationDate
    );

    @Query("""
       SELECT b FROM Batch b
       WHERE b.expirationDate <= :thresholdDate
       AND b.currentQuantity > 0
       AND b.batchStatus = 'ACTIVE'
       ORDER BY b.expirationDate ASC
    """)
    List<Batch> findBatchesNearExpiration(LocalDate thresholdDate);
}
