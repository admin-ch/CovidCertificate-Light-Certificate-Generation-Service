package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class LightCertificate {
    @JsonProperty("ver")
    private String version;
    @JsonProperty("nam")
    private LightCertificatePersonName name;
    @JsonProperty("dob")
    private String dateOfBirth;
    @JsonIgnore
    private Instant expirationInstant;
}