package tech.fefofifodemo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.fefofifodemo.domain.enums.ExitReason;

import java.time.LocalDate;

@Entity
@Table(name = "stock_exits")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockExit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate exitDate;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    private ExitReason exitReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;
}
