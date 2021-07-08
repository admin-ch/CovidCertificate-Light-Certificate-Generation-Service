package ch.admin.bag.covidcertificate.api.incoming;

import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePersonName;
import ch.admin.bag.covidcertificate.service.domain.LightCertificate;
import com.google.common.collect.Comparators;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static ch.admin.bag.covidcertificate.api.Constants.CERTIFICATE_VERSION;

@Component
public class LightCertificateMapper {

    @Value("${light-certificate-generation-service.certificate.validity-in-hours}")
    private int validityInHours;

    public LightCertificate toLightCertificate(
            LightCertificateCreateDto lightCertificateCreateDto
    ) {
        return new LightCertificate(
                CERTIFICATE_VERSION,
                toCovidCertificatePersonName(lightCertificateCreateDto.getName()),
                lightCertificateCreateDto.getDateOfBirth(),
                computeValidity(lightCertificateCreateDto)
        );
    }

    private CovidCertificatePersonName toCovidCertificatePersonName(CovidCertificatePersonNameDto covidCertificatePersonNameDto){
        return new CovidCertificatePersonName(
                covidCertificatePersonNameDto.getFamilyName(),
                covidCertificatePersonNameDto.getFamilyNameStandardised(),
                covidCertificatePersonNameDto.getGivenName(),
                covidCertificatePersonNameDto.getGivenNameStandardised()
        );
    }

    private Instant computeValidity(LightCertificateCreateDto lightCertificateCreateDto){
        var euCertificateExpirationInstant = Instant.ofEpochMilli(lightCertificateCreateDto.getExpiryDate());
        var lightCertificateMaxExpirationDate = LocalDateTime.now().plusHours(validityInHours);
        var lightCertificateMaxExpirationInstant = lightCertificateMaxExpirationDate
                .atOffset(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now()))
                .toInstant();

        return Comparators.min(euCertificateExpirationInstant, lightCertificateMaxExpirationInstant);
    }
}