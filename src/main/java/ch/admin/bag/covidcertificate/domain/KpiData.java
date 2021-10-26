package ch.admin.bag.covidcertificate.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "kpi")
@EqualsAndHashCode(exclude = {"id"})
public class KpiData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    LocalDateTime timestamp;
    String type;
    String value;

    public KpiData(LocalDateTime timestamp, String type, String value) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
    }
}