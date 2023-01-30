package ch.admin.bag.covidcertificate.api.incoming;

import ch.admin.bag.covidcertificate.api.validation.ValidExpirationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class LightCertificateCreateDto {
    @JsonProperty("nam")
    @NotNull
    private @Valid LightCertificatePersonNameDto name;
    @JsonProperty("dob")
    private String dateOfBirth;
    @JsonProperty("exp")
    @ValidExpirationTimestamp
    private Long expiryDate;
}