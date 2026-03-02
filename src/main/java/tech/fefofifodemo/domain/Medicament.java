package tech.fefofifodemo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.fefofifodemo.domain.enums.MedicamentCategory;
import tech.fefofifodemo.domain.enums.UnitOfMeasure;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "medicaments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicamentCategory medicamentCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column(nullable = false)
    private Boolean coldChain;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal volume;

    @OneToMany(mappedBy = "medicament", fetch = FetchType.LAZY)
    private List<Batch> batches;

}
