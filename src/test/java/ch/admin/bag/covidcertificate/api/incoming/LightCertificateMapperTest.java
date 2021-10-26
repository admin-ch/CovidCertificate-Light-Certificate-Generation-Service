package ch.admin.bag.covidcertificate.api.incoming;

import ch.admin.bag.covidcertificate.service.domain.LightCertificatePersonName;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeLightCertificateCreateDto;
import static ch.admin.bag.covidcertificate.api.Constants.CERTIFICATE_VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LightCertificateMapperTest {
    @InjectMocks
    private LightCertificateMapper lightCertificateMapper;

    private final JFixture fixture = new JFixture();
    private static final int validityInHours = 48;

    @BeforeEach
    private void init() {
        ReflectionTestUtils.setField(lightCertificateMapper, "validityInHours", validityInHours);
        customizeLightCertificateCreateDto(fixture);
    }

    @Test
    void shouldMapVersion() {
        var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
        var actual = lightCertificateMapper.toLightCertificate(lightCertificateCreateDto);
        assertEquals(CERTIFICATE_VERSION, actual.getVersion());
    }

    @Test
    void shouldMapName() {
        var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
        var lightCertificatePersonName = fixture.create(LightCertificatePersonName.class);

        try (MockedStatic<LightCertificatePersonNameMapper> lightCertificatePersonNameMapperMock = Mockito.mockStatic(LightCertificatePersonNameMapper.class, Mockito.CALLS_REAL_METHODS)) {
            lightCertificatePersonNameMapperMock.when(() -> LightCertificatePersonNameMapper.toLightCertificatePersonName(any())).thenReturn(lightCertificatePersonName);
            var actual = lightCertificateMapper.toLightCertificate(lightCertificateCreateDto);

            lightCertificatePersonNameMapperMock.verify(() -> LightCertificatePersonNameMapper.toLightCertificatePersonName(lightCertificateCreateDto.getName()));
            assertEquals(lightCertificatePersonName, actual.getName());
        }
    }

    @Test
    void shouldMapDateOfBirth() {
        var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
        var actual = lightCertificateMapper.toLightCertificate(lightCertificateCreateDto);
        assertEquals(lightCertificateCreateDto.getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    void shouldMapExpirationInstant_toCurrentTimestampPlusValidityInHours_ifExpiryDateIsAfterCurrentTimestampPlusValidityInHours() {
        var now = fixture.create(LocalDateTime.class);

        try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
            var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);

            var actual = lightCertificateMapper.toLightCertificate(lightCertificateCreateDto);
            assertEquals(getInstant(now.plusHours(validityInHours)).truncatedTo(ChronoUnit.MILLIS), actual.getExpirationInstant().truncatedTo(ChronoUnit.MILLIS));
        }
    }

    @Test
    void shouldMapExpirationInstant_toExpiryDate_ifExpiryDateIsBeforeCurrentTimestampPlusValidityInHours() {
        var now = fixture.create(LocalDateTime.class);

        try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
            var lightCertificateCreateDto = fixture.create(LightCertificateCreateDto.class);
            var expiryInstant = getInstant(now.plusHours(10));
            ReflectionTestUtils.setField(lightCertificateCreateDto, "expiryDate", expiryInstant.toEpochMilli());

            var actual = lightCertificateMapper.toLightCertificate(lightCertificateCreateDto);
            assertEquals(expiryInstant.truncatedTo(ChronoUnit.MILLIS), actual.getExpirationInstant().truncatedTo(ChronoUnit.MILLIS));
        }
    }

    private Instant getInstant(LocalDateTime localDateTime) {
        return localDateTime
                .atOffset(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now()))
                .toInstant();
    }
}