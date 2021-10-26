package ch.admin.bag.covidcertificate.api.incoming;

import ch.admin.bag.covidcertificate.service.domain.LightCertificatePersonName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LightCertificatePersonNameMapper {

    static LightCertificatePersonName toLightCertificatePersonName(LightCertificatePersonNameDto lightCertificatePersonNameDto){
        return new LightCertificatePersonName(
                lightCertificatePersonNameDto.getFamilyName(),
                lightCertificatePersonNameDto.getFamilyNameStandardised(),
                lightCertificatePersonNameDto.getGivenName(),
                lightCertificatePersonNameDto.getGivenNameStandardised()
        );
    }
}