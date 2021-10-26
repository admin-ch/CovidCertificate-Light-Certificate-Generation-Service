package ch.admin.bag.covidcertificate.service;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwissDGCSignerTest {
    @InjectMocks
    private SwissDGCSigner swissDGCSigner;
    @Mock
    private COSEService coseService;

    private final JFixture fixture = new JFixture();

    @Nested
    class Sign {
        @Test
        void shouldSignCertificate() {
            var dgcCBOR = fixture.create(byte[].class);
            var expiryDate = fixture.create(Instant.class);
            swissDGCSigner.sign(dgcCBOR, expiryDate);
            verify(coseService).getCOSESign1(dgcCBOR, expiryDate);
        }

        @Test
        void whenSign_thenOk() {
            // given
            byte[] signedCbor = fixture.create(byte[].class);
            when(coseService.getCOSESign1(any(byte[].class), any())).thenReturn(signedCbor);
            // when
            byte[] result = swissDGCSigner.sign(fixture.create(byte[].class), Instant.now());
            // then
            assertEquals(signedCbor, result);
        }

    }

    @Nested
    class GetSignerExpiration{
        @Test
        void whenGetSignerExpiration_thenThrowsUnsupportedOperationException() {
            // when then
            assertThrows(UnsupportedOperationException.class, () -> swissDGCSigner.getSignerExpiration());
        }
    }

    @Nested
    class GetSignerCountry{
        @Test
        void whenGetSignerCountry_thenThrowsUnsupportedOperationException() {
            // when then
            assertThrows(UnsupportedOperationException.class, () -> swissDGCSigner.getSignerCountry());
        }
    }
}
