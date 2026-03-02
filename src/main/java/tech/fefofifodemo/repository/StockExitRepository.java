package tech.fefofifodemo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.fefofifodemo.domain.StockExit;

@Repository
public interface StockExitRepository extends JpaRepository<StockExit, Long> {
    Page<StockExit> findStockExitsByBatchId(Pageable pageable, Long batchId);
}
