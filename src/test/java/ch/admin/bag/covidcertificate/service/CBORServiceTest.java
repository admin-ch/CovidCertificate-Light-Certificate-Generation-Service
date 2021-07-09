package ch.admin.bag.covidcertificate.service;

import com.flextrade.jfixture.JFixture;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.digg.dgc.signatures.cwt.support.CBORInstantConverter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CBORServiceTest {
    @InjectMocks
    private CBORService cborService;
    @Mock
    private COSETime coseTime;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    private void init(){
        byte[] keyIdentifierBytes = fixture.create(String.class).getBytes();
        String keyIdentifier = new String(Hex.encodeHex(keyIdentifierBytes));
        ReflectionTestUtils.setField(cborService, "keyIdentifier", keyIdentifier);
        lenient().when(coseTime.getIssuedAt()).thenReturn(fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS));
    }

    @Nested
    class GetProtectedHeader{
        @Test
        void shouldReturnKeyIdentifier() throws Exception {
            // given
            byte[] keyIdentifierBytes = fixture.create(String.class).getBytes();
            String keyIdentifier = encodeToHexString(keyIdentifierBytes);
            ReflectionTestUtils.setField(cborService, "keyIdentifier", keyIdentifier);
            // when
            byte[] result = cborService.getProtectedHeader();
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(keyIdentifier, encodeToHexString(resultCBORObject.get(CBORObject.FromObject(4)).GetByteString()));
        }

        @Test
        void shouldReturnSigningAlgorithm() throws Exception {
            byte[] result = cborService.getProtectedHeader();
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(-37, resultCBORObject.get(CBORObject.FromObject(1)).AsInt32());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldThrowsIllegalArgumentException_ifKeyIdentifierIsNullOrEmpty(String keyIdentifier) {
            // when then
            ReflectionTestUtils.setField(cborService, "keyIdentifier", keyIdentifier);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    cborService::getProtectedHeader);
            assertTrue(exception.getMessage().toLowerCase().contains("keyidentifier"));
        }
    }

    @Nested
    class GetPayload{
        @Test
        void shouldReturnIssuer() {
            Instant expiration = fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS);
            byte[] hcert = fixture.create(CBORObject.class).EncodeToBytes();
            // when
            byte[] result = cborService.getPayload(hcert, expiration);
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals("CH", resultCBORObject.get(1).AsString());
        }

        @Test
        void shouldReturnIssuedAt() {
            CBORInstantConverter instantConverter = new CBORInstantConverter();
            // given
            Instant issuedAt = fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS);
            Instant expiration = fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS);
            when(coseTime.getIssuedAt()).thenReturn(issuedAt);
            byte[] hcert = fixture.create(CBORObject.class).EncodeToBytes();
            // when
            byte[] result = cborService.getPayload(hcert, expiration);
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(issuedAt, instantConverter.FromCBORObject(resultCBORObject.get(6)));
        }

        @Test
        void shouldReturnExpiration() {
            CBORInstantConverter instantConverter = new CBORInstantConverter();
            // given
            Instant expiration = fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS);
            byte[] hcert = fixture.create(CBORObject.class).EncodeToBytes();
            // when
            byte[] result = cborService.getPayload(hcert, expiration);
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(expiration, instantConverter.FromCBORObject(resultCBORObject.get(4)));
        }

        @Test
        void shouldReturnHCert() {
            Instant expiration = fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS);
            byte[] hcert = fixture.create(CBORObject.class).EncodeToBytes();
            // when
            byte[] result = cborService.getPayload(hcert, expiration);
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(hcert, resultCBORObject.get(-250).get(1).EncodeToBytes());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowsIllegalArgumentException_ifHcertIsNullOrEmpty(byte[] hcert) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getPayload(hcert, null));
            assertTrue(exception.getMessage().toLowerCase().contains("hcert"));
        }
    }

    @Nested
    class GetSignatureData{
        @Test
        void shouldReturnContext() {
            var result = cborService.getSignatureData(fixture.create(byte[].class), fixture.create(byte[].class));
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals("Signature1", resultCBORObject.get(0).AsString());
        }

        @Test
        void shouldReturnProtectedHeader() {
            // given
            byte[] coseProtectedHeader = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getSignatureData(coseProtectedHeader, fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(coseProtectedHeader, resultCBORObject.get(1).GetByteString());
        }

        @Test
        void shouldReturnEmptyExternalAAD() {
            // when
            byte[] result = cborService.getSignatureData(fixture.create(byte[].class), fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(new byte[0], resultCBORObject.get(2).GetByteString());
        }

        @Test
        void shouldReturnPayload() {
            // given
            byte[] payload = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getSignatureData(fixture.create(byte[].class), payload);
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(payload, resultCBORObject.get(3).GetByteString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowsIllegalArgumentException_ifProtectedBodyIsNullOrEmpty(byte[] bodyProtected) {
            // when then
            byte[] payload = fixture.create(byte[].class);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getSignatureData(bodyProtected, payload));
            assertTrue(exception.getMessage().toLowerCase().contains("bodyprotected"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowsIllegalArgumentException_ifPayloadIsNullOrEmpty(byte[] payload) {
            // when then
            byte[] bodyProtected = fixture.create(byte[].class);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getSignatureData(bodyProtected, payload));
            assertTrue(exception.getMessage().toLowerCase().contains("payload"));
        }
    }

    @Nested
    class GetCOSESign1{
        @Test
        void shouldReturnProtectedHeader() {
            // given
            byte[] coseProtectedHeader = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getCOSESign1(coseProtectedHeader, fixture.create(byte[].class), fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(coseProtectedHeader, resultCBORObject.get(0).GetByteString());
        }

        @Test
        void shouldReturnEmptyUnprotectedHeader() {
            // when
            byte[] result = cborService.getCOSESign1(fixture.create(byte[].class), fixture.create(byte[].class), fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(CBORObject.NewMap(), resultCBORObject.get(1));
        }

        @Test
        void shouldReturnPayload() {
            // given
            byte[] payload = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getCOSESign1(fixture.create(byte[].class), payload, fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(payload, resultCBORObject.get(2).GetByteString());
        }

        @Test
        void shouldReturnSignature() {
            // given
            byte[] signature = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getCOSESign1(fixture.create(byte[].class), fixture.create(byte[].class), signature);
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(signature, resultCBORObject.get(3).GetByteString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowsIllegalArgumentException_ifProtectedHeaderIsNullOrEmpty(byte[] protectedHeader) {
            // when then
            byte[] bytesMock = fixture.create(byte[].class);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getCOSESign1(protectedHeader, bytesMock, bytesMock));
            assertTrue(exception.getMessage().toLowerCase().contains("protectedheader"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowsIllegalArgumentException_ifPayloadIsNullOrEmpty(byte[] payload) {
            // when then
            byte[] bytesMock = fixture.create(byte[].class);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getCOSESign1(bytesMock, payload, bytesMock));
            assertTrue(exception.getMessage().toLowerCase().contains("payload"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowsIllegalArgumentException_ifSignatureIsNullOrEmpty(byte[] signature) {
            // given
            byte[] bytesMock = fixture.create(byte[].class);
            // when then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getCOSESign1(bytesMock, bytesMock, signature));
            assertTrue(exception.getMessage().toLowerCase().contains("signature"));
        }
    }

    private String encodeToHexString(byte[] bytes){
        return new String(Hex.encodeHex(bytes));
    }
}
