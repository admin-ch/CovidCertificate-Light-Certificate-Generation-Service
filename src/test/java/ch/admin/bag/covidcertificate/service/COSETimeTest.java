package ch.admin.bag.covidcertificate.service;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class COSETimeTest {
    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Zurich");
    private final JFixture jFixture = new JFixture();
    private Instant instant;
    private COSETime coseTime;

    @BeforeEach
    private void init() {
        // given
        instant = jFixture.create(Instant.class);
        coseTime = new COSETime(Clock.fixed(instant, ZONE_ID));
    }

    @Test
    void whenGetIssuedAt_thenOk() {
        // when
        Instant result = coseTime.getIssuedAt();
        // then
        assertEquals(instant, result);
    }
}
