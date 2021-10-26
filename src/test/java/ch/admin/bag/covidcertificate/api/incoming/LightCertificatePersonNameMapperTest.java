package ch.admin.bag.covidcertificate.api.incoming;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LightCertificatePersonNameMapperTest {
    private final JFixture fixture = new JFixture();

    @Test
    void shouldMapFamilyName(){
        var lightCertificatePersonNameDto = fixture.create(LightCertificatePersonNameDto.class);
        var actual = LightCertificatePersonNameMapper.toLightCertificatePersonName(lightCertificatePersonNameDto);
        assertEquals(lightCertificatePersonNameDto.getFamilyName(), actual.getFamilyName());
    }

    @Test
    void shouldMapFamilyNameStandardised(){
        var lightCertificatePersonNameDto = fixture.create(LightCertificatePersonNameDto.class);
        var actual = LightCertificatePersonNameMapper.toLightCertificatePersonName(lightCertificatePersonNameDto);
        assertEquals(lightCertificatePersonNameDto.getFamilyNameStandardised(), actual.getFamilyNameStandardised());
    }

    @Test
    void shouldMapGivenName(){
        var lightCertificatePersonNameDto = fixture.create(LightCertificatePersonNameDto.class);
        var actual = LightCertificatePersonNameMapper.toLightCertificatePersonName(lightCertificatePersonNameDto);
        assertEquals(lightCertificatePersonNameDto.getGivenName(), actual.getGivenName());
    }

    @Test
    void shouldMapGivenNameStandardised(){
        var lightCertificatePersonNameDto = fixture.create(LightCertificatePersonNameDto.class);
        var actual = LightCertificatePersonNameMapper.toLightCertificatePersonName(lightCertificatePersonNameDto);
        assertEquals(lightCertificatePersonNameDto.getGivenNameStandardised(), actual.getGivenNameStandardised());
    }
}