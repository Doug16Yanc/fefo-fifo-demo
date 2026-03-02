package tech.fefofifodemo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.fefofifodemo.domain.Batch;
import tech.fefofifodemo.domain.ExpirationAlert;
import tech.fefofifodemo.domain.enums.ExpirationAlertStatus;

@Repository
public interface ExpirationAlertRepository extends JpaRepository<ExpirationAlert, Long> {

    @Query("SELECT COUNT(e) > 0 FROM ExpirationAlert e " +
            "WHERE e.batch = :batch " +
            "AND e.expirationAlertStatus = :status")
    boolean existsByBatchAndStatus(@Param("batch") Batch batch,
                                   @Param("status") ExpirationAlertStatus status);

    Page<ExpirationAlert> findByExpirationAlertStatus(
            ExpirationAlertStatus status,
            Pageable pageable
    );}
