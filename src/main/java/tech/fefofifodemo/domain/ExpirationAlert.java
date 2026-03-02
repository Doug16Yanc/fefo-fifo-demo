package tech.fefofifodemo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.fefofifodemo.domain.enums.ExpirationAlertStatus;

import java.time.LocalDate;

@Entity
@Table(name = "expiration_alerts", indexes = {
        @Index(name = "idx_alert_status_date", columnList = "expirationAlertStatus, alertDate"),
        @Index(name = "idx_alert_batch_id", columnList = "batch_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpirationAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate alertDate;

    @Column(nullable = false)
    private int daysUntilExpiration;

    @Enumerated(EnumType.STRING)
    private ExpirationAlertStatus expirationAlertStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;
}
