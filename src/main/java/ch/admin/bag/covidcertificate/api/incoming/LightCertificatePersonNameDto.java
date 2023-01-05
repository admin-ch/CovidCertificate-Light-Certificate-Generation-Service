package ch.admin.bag.covidcertificate.api.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class LightCertificatePersonNameDto {
    @JsonProperty("fn")
    @NotEmpty
    private String familyName;
    @JsonProperty("fnt")
    private String familyNameStandardised;
    @JsonProperty("gn")
    @NotEmpty
    private String givenName;
    @JsonProperty("gnt")
    private String givenNameStandardised;
}
