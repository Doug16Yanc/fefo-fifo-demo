package tech.fefofifodemo.domain.views;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vw_batch_fefo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Immutable
@Subselect("SELECT * FROM vw_batch_fefo")
@Synchronize({"batches", "medicaments"})
public class BatchFefo {

    @Id
    private Long batchId;
    private Long batchNumber;
    private LocalDate expirationDate;
    private int currentQuantity;
    private String batchStatus;
    private Long medicamentId;
    private String medicamentName;
    private BigDecimal volume;
    private String medicamentCategory;
}
