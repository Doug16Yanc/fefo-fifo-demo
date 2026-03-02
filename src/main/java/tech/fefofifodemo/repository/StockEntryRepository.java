package tech.fefofifodemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.fefofifodemo.domain.StockEntry;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
}
