package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.api.incoming.LightCertificateCreateDto;
import com.flextrade.jfixture.JFixture;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class FixtureCustomization {
    public static void customizeLightCertificateCreateDto(JFixture fixture){
        fixture.customise().lazyInstance(LightCertificateCreateDto.class, () -> {
            var expiryDate = Instant.now().plus(10, ChronoUnit.DAYS);
            var lightCertificateCreateDto = new JFixture().create(LightCertificateCreateDto.class);
            ReflectionTestUtils.setField(lightCertificateCreateDto, "expiryDate", expiryDate.toEpochMilli());

            return lightCertificateCreateDto;
        });
    }
}
