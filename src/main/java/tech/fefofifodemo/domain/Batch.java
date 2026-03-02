package tech.fefofifodemo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.fefofifodemo.domain.enums.BatchStatus;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "batches")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long batchNumber;

    @Column(nullable = false)
    private LocalDate manufacturingDate;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private int initialQuantity;

    @Column(nullable = false)
    private int currentQuantity;

    @Enumerated(EnumType.STRING)
    private BatchStatus batchStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id")
    private Medicament medicament;

    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY)
    private List<StockEntry> stockEntries;

    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY)
    private List<StockExit> stockExists;

    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY)
    private List<ExpirationAlert> expirationAlerts;
}
