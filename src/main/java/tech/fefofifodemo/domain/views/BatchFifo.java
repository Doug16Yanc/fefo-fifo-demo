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

import java.time.LocalDate;

@Entity
@Table(name = "vw_batch_fifo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Immutable
@Subselect("SELECT * FROM vw_batch_fifo")
@Synchronize({"batches, medicaments"})
public class BatchFifo {

    @Id
    private Long batchId;
    private Long batchNumber;
    private LocalDate expirationDate;
    private String batchStatus;
    private Long medicamentId;
    private String medicamentName;
    private String medicationCategory;
    private LocalDate entryDate;
}
